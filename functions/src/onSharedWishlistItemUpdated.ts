import * as admin from "firebase-admin";
import { onDocumentUpdated } from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";

const db = admin.firestore();

type SharedWishlistEntity = {
  id?: string;
  wishlist?: string | null;
  owner?: string | null;
  editors?: string[];
  group?: string | null;
  participants?: string[];
  editorsCanSeeUpdates?: boolean;
};

type WishlistEntity = {
  id?: string;
  title?: string;
  photo?: {
    type?: "Url" | "Preset";
    value?: string | null;
  };
};

type WishlistItemEntity = {
  id?: string;
  name?: string;
  photoUrl?: string | null;
};

type GroupEntity = {
  members?: string[];
};

type SharedWishlistItemEntity = {
  id?: string;
  item?: string | null;
  state?: "Available" | "Reserved" | "Purchased" | "ShareRequest";
  reservation?: {
    reservedBy?: string | null;
    reservedAt?: number | null;
    expiresAt?: number | null;
  } | null;
  shareRequest?: {
    createdBy?: string | null;
    participantsRequested?: number | null;
    participantsJoined?: string[];
    requestedAt?: number | null;
    expiresAt?: number | null;
  } | null;
  purchased?: {
    purchasedAt?: number | null;
    purchasedBy?: string | null;
  } | null;
};

type UserEntity = {
  username?: string;
  token?: string | null;
  notifications?: {
    sharedWishlistUpdates?: boolean;
  };
};

type PushData = Record<string, string>;

type UpdateEvent =
  | {
      kind: "reservation_created";
      actorUid: string | null;
      body: string;
    }
  | {
      kind: "reservation_cleared";
      actorUid: string | null;
      body: string;
    }
  | {
      kind: "share_request_created";
      actorUid: string | null;
      body: string;
    }
  | {
      kind: "share_request_joined";
      actorUid: string | null;
      body: string;
    }
  | {
      kind: "share_request_cleared";
      actorUid: string | null;
      body: string;
    }
  | {
      kind: "purchased_created";
      actorUid: string | null;
      body: string;
    }
  | {
      kind: "purchased_cleared";
      actorUid: string | null;
      body: string;
    };

export const onSharedWishlistItemUpdated = onDocumentUpdated(
  {
    document: "shared-wishlists/{sharedWishlistId}/items/{sharedWishlistItemId}",
    region: "europe-west1",
    maxInstances: 1,
  },
  async (event) => {
    const beforeSnap = event.data?.before;
    const afterSnap = event.data?.after;

    if (!beforeSnap || !afterSnap) {
      logger.warn("Missing before/after snapshot in onSharedWishlistItemUpdated");
      return;
    }

    const { sharedWishlistId, sharedWishlistItemId } = event.params;
    const before = beforeSnap.data() as SharedWishlistItemEntity;
    const after = afterSnap.data() as SharedWishlistItemEntity;

    const sharedWishlistSnap = await db
      .collection("shared-wishlists")
      .doc(sharedWishlistId)
      .get();

    if (!sharedWishlistSnap.exists) {
      logger.warn("Shared wishlist not found for item update notification", {
        sharedWishlistId,
        sharedWishlistItemId,
      });
      return;
    }

    const sharedWishlist = sharedWishlistSnap.data() as SharedWishlistEntity;

    const linkedWishlistId = ensureOptionalString(sharedWishlist.wishlist);
    const linkedItemId = ensureOptionalString(after.item) ?? ensureOptionalString(before.item);

    const [linkedWishlist, linkedItem] = await Promise.all([
      linkedWishlistId ? resolveWishlist(linkedWishlistId) : Promise.resolve(null),
      linkedWishlistId && linkedItemId
        ? resolveWishlistItem(linkedWishlistId, linkedItemId)
        : Promise.resolve(null),
    ]);

    const updateEvent = await resolveUpdateEvent({
      before,
      after,
      itemName: linkedItem?.name?.trim() || "article",
    });

    if (!updateEvent) {
      logger.info("No relevant shared wishlist item transition detected", {
        sharedWishlistId,
        sharedWishlistItemId,
      });
      return;
    }

    const recipientUids = await resolveRecipientUidsForUpdates(
      sharedWishlist,
      updateEvent.actorUid
    );

    if (recipientUids.length === 0) {
      logger.info("No recipients for shared wishlist item update notification", {
        sharedWishlistId,
        sharedWishlistItemId,
        kind: updateEvent.kind,
      });
      return;
    }

    const tokens = await resolveEnabledTokensForSharedWishlistUpdates(recipientUids);

    if (tokens.length === 0) {
      logger.info("No valid tokens for shared wishlist item update notification", {
        sharedWishlistId,
        sharedWishlistItemId,
        kind: updateEvent.kind,
        recipients: recipientUids.length,
      });
      return;
    }

    const wishlistTitle = escapeHtml(
      linkedWishlist?.title?.trim() || "Shared wishlist"
    );

    const deeplink = `https://www.wishlify.com/shared-wishlist/${sharedWishlistId}`;

    const pushData: PushData = {
      type: "update",
      title: `<b>${wishlistTitle}</b> | Shared Wishlist`,
      body: updateEvent.body,
      deeplink,
    };

    if (typeof linkedItem?.photoUrl === "string" && linkedItem.photoUrl.trim().length > 0) {
      pushData.imageUrl = linkedItem.photoUrl.trim();
    } else if (
      typeof linkedWishlist?.photo?.type === "string" &&
      typeof linkedWishlist?.photo?.value === "string" &&
      linkedWishlist.photo.type === "Url" &&
      linkedWishlist.photo.value.trim().length > 0
    ) {
      pushData.imageUrl = linkedWishlist.photo.value.trim();
    }

    const response = await admin.messaging().sendEachForMulticast({
      tokens,
      data: pushData,
      android: {
        priority: "high",
      },
    });

    logger.info("Shared wishlist item update push processed", {
      sharedWishlistId,
      sharedWishlistItemId,
      kind: updateEvent.kind,
      recipients: recipientUids.length,
      tokens: tokens.length,
      successCount: response.successCount,
      failureCount: response.failureCount,
    });
  }
);

async function resolveUpdateEvent(params: {
  before: SharedWishlistItemEntity;
  after: SharedWishlistItemEntity;
  itemName: string;
}): Promise<UpdateEvent | null> {
  const { before, after, itemName } = params;
  const escapedItemName = escapeHtml(itemName);

  const beforeReservedBy = ensureOptionalString(before.reservation?.reservedBy);
  const afterReservedBy = ensureOptionalString(after.reservation?.reservedBy);

  if (!beforeReservedBy && afterReservedBy) {
    const actorName = await resolveDisplayNameOrFallback(afterReservedBy);
    return {
      kind: "reservation_created",
      actorUid: afterReservedBy,
      body: `<b>${escapeHtml(actorName)}</b> ha reservat <b>${escapedItemName}</b>`,
    };
  }

  if (beforeReservedBy && !afterReservedBy) {
    return {
      kind: "reservation_cleared",
      actorUid: beforeReservedBy,
      body: `La reserva de <b>${escapedItemName}</b> s'ha cancel·lat`,
    };
  }

  const beforeShareRequestCreatedBy = ensureOptionalString(before.shareRequest?.createdBy);
  const afterShareRequestCreatedBy = ensureOptionalString(after.shareRequest?.createdBy);

  if (!beforeShareRequestCreatedBy && afterShareRequestCreatedBy) {
    const actorName = await resolveDisplayNameOrFallback(afterShareRequestCreatedBy);
    return {
      kind: "share_request_created",
      actorUid: afterShareRequestCreatedBy,
      body: `<b>${escapeHtml(actorName)}</b> ha creat una sol·licitud per compartir <b>${escapedItemName}</b>`,
    };
  }

  const beforeJoined = ensureStringArray(before.shareRequest?.participantsJoined);
  const afterJoined = ensureStringArray(after.shareRequest?.participantsJoined);
  const newlyJoined = afterJoined.find((uid) => !beforeJoined.includes(uid));

  if (newlyJoined) {
    const actorName = await resolveDisplayNameOrFallback(newlyJoined);
    return {
      kind: "share_request_joined",
      actorUid: newlyJoined,
      body: `<b>${escapeHtml(actorName)}</b> s'ha unit a la sol·licitud de compartir de <b>${escapedItemName}</b>`,
    };
  }

  if (beforeShareRequestCreatedBy && !afterShareRequestCreatedBy) {
    return {
      kind: "share_request_cleared",
      actorUid: beforeShareRequestCreatedBy,
      body: `La sol·licitud de compartir de <b>${escapedItemName}</b> s'ha cancel·lat`,
    };
  }

  const beforePurchasedBy = ensureOptionalString(before.purchased?.purchasedBy);
  const afterPurchasedBy = ensureOptionalString(after.purchased?.purchasedBy);

  if (!beforePurchasedBy && afterPurchasedBy) {
    const actorName = await resolveDisplayNameOrFallback(afterPurchasedBy);
    return {
      kind: "purchased_created",
      actorUid: afterPurchasedBy,
      body: `<b>${escapeHtml(actorName)}</b> ha marcat <b>${escapedItemName}</b> com a comprat`,
    };
  }

  if (beforePurchasedBy && !afterPurchasedBy) {
    return {
      kind: "purchased_cleared",
      actorUid: beforePurchasedBy,
      body: `<b>${escapedItemName}</b> ja no consta com a comprat`,
    };
  }

  return null;
}

async function resolveRecipientUidsForUpdates(
  sharedWishlist: SharedWishlistEntity,
  actorUid: string | null
): Promise<string[]> {
  const owner = ensureOptionalString(sharedWishlist.owner);
  const participants = ensureStringArray(sharedWishlist.participants);
  const groupMembers = await resolveGroupMembers(sharedWishlist.group);
  const editors =
    sharedWishlist.editorsCanSeeUpdates === true
      ? ensureStringArray(sharedWishlist.editors)
      : [];

  return Array.from(
    new Set([
      ...(owner ? [owner] : []),
      ...participants,
      ...groupMembers,
      ...editors,
    ])
  ).filter((uid) => uid !== actorUid);
}

async function resolveGroupMembers(groupId: string | null | undefined): Promise<string[]> {
  const normalizedGroupId = ensureOptionalString(groupId);
  if (!normalizedGroupId) {
    return [];
  }

  const groupSnap = await db.collection("groups").doc(normalizedGroupId).get();
  if (!groupSnap.exists) {
    return [];
  }

  const group = groupSnap.data() as GroupEntity;
  return ensureStringArray(group.members);
}

async function resolveWishlist(wishlistId: string): Promise<WishlistEntity | null> {
  const snap = await db.collection("wishlists").doc(wishlistId).get();
  if (!snap.exists) {
    return null;
  }

  return snap.data() as WishlistEntity;
}

async function resolveWishlistItem(
  wishlistId: string,
  itemId: string
): Promise<WishlistItemEntity | null> {
  const snap = await db
    .collection("wishlists")
    .doc(wishlistId)
    .collection("items")
    .doc(itemId)
    .get();

  if (!snap.exists) {
    return null;
  }

  return snap.data() as WishlistItemEntity;
}

async function resolveUser(uid: string): Promise<UserEntity | null> {
  const snap = await db.collection("users").doc(uid).get();
  if (!snap.exists) {
    return null;
  }

  return snap.data() as UserEntity;
}

async function resolveDisplayNameOrFallback(uid: string): Promise<string> {
  const user = await resolveUser(uid);
  return typeof user?.username === "string" && user.username.trim().length > 0
    ? user.username
    : "Algú";
}

async function resolveEnabledTokensForSharedWishlistUpdates(
  userIds: string[]
): Promise<string[]> {
  const userSnaps = await Promise.all(
    userIds.map((uid) => db.collection("users").doc(uid).get())
  );

  return userSnaps
    .filter((snap) => snap.exists)
    .map((snap) => snap.data() as UserEntity)
    .filter((user) => user.notifications?.sharedWishlistUpdates === true)
    .map((user) => user.token)
    .filter((token): token is string => typeof token === "string" && token.trim().length > 0);
}

function ensureStringArray(value: unknown): string[] {
  return Array.isArray(value)
    ? value.filter((item): item is string => typeof item === "string" && item.trim().length > 0)
    : [];
}

function ensureOptionalString(value: unknown): string | null {
  return typeof value === "string" && value.trim().length > 0
    ? value.trim()
    : null;
}

function escapeHtml(value: string): string {
  return value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}