import * as admin from "firebase-admin";
import { onDocumentCreated } from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";

const db = admin.firestore();

type SharedWishlistEntity = {
  id?: string;
  wishlist?: string | null;
  owner?: string;
  editors?: string[];
  group?: string | null;
  participants?: string[];
  editorsCanSeeUpdates?: boolean;
  inviteLink?: string | null;
  deadline?: number | null;
  sharedAt?: number | null;
};

type WishlistEntity = {
  id?: string;
  title?: string;
  description?: string | null;
  photoUrl?: string | null;
  type?: string;
  target?: string | null;
  editorInviteLink?: string | null;
  editors?: string[];
  shareStatus?: string;
  sharedWishlistId?: string | null;
  createdBy?: string;
  createdAt?: number;
  lastUpdate?: {
    updatedBy?: string;
    updatedAt?: number;
  } | null;
};

type GroupEntity = {
  members?: string[];
};

type SharedWishlistChatMessageEntity = {
  id?: string;
  createdBy?: string;
  text?: string;
  type?: string;
};

type UserEntity = {
  username?: string;
  token?: string | null;
  notifications?: {
    sharedWishlistChat?: boolean;
  };
};

type PushData = Record<string, string>;

const CHAT_MESSAGE_MAX_LENGTH = 80;

export const onSharedWishlistChatMessageCreated = onDocumentCreated(
  {
    document: "shared-wishlists/{sharedWishlistId}/chat/{messageId}",
    region: "europe-west1",
    maxInstances: 1,
  },
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) {
      logger.warn("Missing snapshot in onSharedWishlistChatMessageCreated");
      return;
    }

    const { sharedWishlistId } = event.params;
    const message = snapshot.data() as SharedWishlistChatMessageEntity;

    const senderUid = message.createdBy;
    if (!senderUid) {
      logger.warn("Shared wishlist chat message without createdBy", {
        sharedWishlistId,
        messageId: snapshot.id,
      });
      return;
    }

    const sharedWishlistSnap = await db
      .collection("shared-wishlists")
      .doc(sharedWishlistId)
      .get();

    if (!sharedWishlistSnap.exists) {
      logger.warn("Shared wishlist not found for chat notification", {
        sharedWishlistId,
        messageId: snapshot.id,
      });
      return;
    }

    const sharedWishlist = sharedWishlistSnap.data() as SharedWishlistEntity;

    const wishlistId =
      typeof sharedWishlist.wishlist === "string" && sharedWishlist.wishlist.trim().length > 0
        ? sharedWishlist.wishlist.trim()
        : null;

    const linkedWishlist = wishlistId
      ? await resolveWishlist(wishlistId)
      : null;

    const recipientUids = await resolveRecipientUids(sharedWishlist, senderUid);

    if (recipientUids.length === 0) {
      logger.info("No recipients for shared wishlist chat notification", {
        sharedWishlistId,
        messageId: snapshot.id,
      });
      return;
    }

    const senderName = await resolveUsername(senderUid);
    const escapedSenderName = escapeHtml(senderName ?? "Algú");
    const escapedWishlistTitle = escapeHtml(
      linkedWishlist?.title?.trim() || "Shared wishlist"
    );
    const escapedMessageText = escapeHtml(
      truncateText(message.text?.trim() || "Nou missatge", CHAT_MESSAGE_MAX_LENGTH)
    );

    const title = `<b>${escapedWishlistTitle}</b> | Shared Wishlist`;
    const body = `<b>${escapedSenderName}</b>: ${escapedMessageText}`;
    const deeplink = `https://www.wishlify.com/shared-wishlist/${sharedWishlistId}/chat`;

    const pushData: PushData = {
      type: "chat",
      title,
      body,
      deeplink,
    };

    if (
      typeof linkedWishlist?.photoUrl === "string" &&
      linkedWishlist.photoUrl.trim().length > 0
    ) {
      pushData.imageUrl = linkedWishlist.photoUrl.trim();
    }

    const tokens = await resolveEnabledTokensForSharedWishlistChat(recipientUids);

    if (tokens.length === 0) {
      logger.info("No valid FCM tokens for shared wishlist chat notification", {
        sharedWishlistId,
        messageId: snapshot.id,
        recipients: recipientUids.length,
      });
      return;
    }

    const response = await admin.messaging().sendEachForMulticast({
      tokens,
      data: pushData,
      android: {
        priority: "high",
      },
    });

    logger.info("Shared wishlist chat push processed", {
      sharedWishlistId,
      messageId: snapshot.id,
      recipients: recipientUids.length,
      tokens: tokens.length,
      successCount: response.successCount,
      failureCount: response.failureCount,
      linkedWishlistId: wishlistId,
    });
  }
);

async function resolveRecipientUids(
  sharedWishlist: SharedWishlistEntity,
  senderUid: string
): Promise<string[]> {
  const editors = ensureStringArray(sharedWishlist.editors);
  const participants = ensureStringArray(sharedWishlist.participants);
  const groupMembers = await resolveGroupMembers(sharedWishlist.group);

  return Array.from(
    new Set([...editors, ...participants, ...groupMembers])
  ).filter((uid) => uid !== senderUid);
}

async function resolveGroupMembers(groupId: string | null | undefined): Promise<string[]> {
  if (!groupId || !groupId.trim()) {
    return [];
  }

  const groupSnap = await db.collection("groups").doc(groupId).get();
  if (!groupSnap.exists) {
    return [];
  }

  const group = groupSnap.data() as GroupEntity;
  return ensureStringArray(group.members);
}

async function resolveWishlist(wishlistId: string): Promise<WishlistEntity | null> {
  const wishlistSnap = await db.collection("wishlists").doc(wishlistId).get();
  if (!wishlistSnap.exists) {
    return null;
  }

  return wishlistSnap.data() as WishlistEntity;
}

async function resolveUsername(uid: string): Promise<string | null> {
  const userSnap = await db.collection("users").doc(uid).get();
  if (!userSnap.exists) {
    return null;
  }

  const user = userSnap.data() as UserEntity;
  return typeof user.username === "string" && user.username.trim().length > 0
    ? user.username
    : null;
}

async function resolveEnabledTokensForSharedWishlistChat(userIds: string[]): Promise<string[]> {
  const userSnaps = await Promise.all(
    userIds.map((uid) => db.collection("users").doc(uid).get())
  );

  return userSnaps
    .filter((snap) => snap.exists)
    .map((snap) => snap.data() as UserEntity)
    .filter((user) => user.notifications?.sharedWishlistChat === true)
    .map((user) => user.token)
    .filter((token): token is string => typeof token === "string" && token.trim().length > 0);
}

function ensureStringArray(value: unknown): string[] {
  return Array.isArray(value)
    ? value.filter((item): item is string => typeof item === "string" && item.trim().length > 0)
    : [];
}

function truncateText(value: string, maxLength: number): string {
  return value.length <= maxLength
    ? value
    : `${value.slice(0, maxLength - 3).trimEnd()}...`;
}

function escapeHtml(value: string): string {
  return value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}