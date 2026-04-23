import * as admin from "firebase-admin";
import { onSchedule } from "firebase-functions/v2/scheduler";
import * as logger from "firebase-functions/logger";

const db = admin.firestore();

const TWO_DAYS_MS = 2 * 24 * 60 * 60 * 1000;

type SharedWishlistEntity = {
  id?: string;
  wishlist?: string | null;
  editors?: string[];
  group?: string | null;
  participants?: string[];
  editorsCanSeeUpdates?: boolean;
  deadline?: number | null;
};

type SecretSantaEntity = {
  id?: string;
  name?: string;
  photoUrl?: string | null;
  participants?: string[];
  group?: string | null;
  createdBy?: string | null;
  deadline?: number | null;
};

type WishlistEntity = {
  id?: string;
  title?: string;
  photoUrl?: string | null;
};

type GroupEntity = {
  members?: string[];
};

type UserEntity = {
  token?: string | null;
  notifications?: {
    sharedWishlistsDeadlineReminders?: boolean;
    secretSantaDeadlineReminders?: boolean;
  };
};

type PushData = Record<string, string>;

export const sendDeadlineRemindersDaily = onSchedule(
  {
    schedule: "0 10 * * *",
    timeZone: "Europe/Madrid",
    region: "europe-west1",
    maxInstances: 1,
  },
  async () => {
    await sendSharedWishlistDeadlineReminders();
    await sendSecretSantaDeadlineReminders();
  }
);

async function sendSharedWishlistDeadlineReminders(): Promise<void> {
  const now = Date.now();
  const sharedWishlistsSnap = await db.collection("shared-wishlists").get();

  for (const sharedWishlistDoc of sharedWishlistsSnap.docs) {
    const sharedWishlist = sharedWishlistDoc.data() as SharedWishlistEntity;
    const sharedWishlistId = sharedWishlistDoc.id;

    const deadline = ensureOptionalNumber(sharedWishlist.deadline);
    if (!shouldSendDeadlineReminder(deadline, now)) {
      continue;
    }

    const reminderId = buildReminderId("shared-wishlist", sharedWishlistId);
    const alreadySent = await existsReminder(reminderId);
    if (alreadySent) {
      continue;
    }

    const linkedWishlistId = ensureOptionalString(sharedWishlist.wishlist);
    const linkedWishlist = linkedWishlistId
      ? await resolveWishlist(linkedWishlistId)
      : null;

    const recipientUids = await resolveSharedWishlistReminderRecipients(sharedWishlist);

    if (recipientUids.length === 0) {
      logger.info("No recipients for shared wishlist reminder", {
        sharedWishlistId,
      });
      await storeReminderSent(reminderId, {
        entityId: sharedWishlistId,
        entityType: "shared-wishlist",
        reminderType: "deadline_minus_2d",
        sentAt: now,
      });
      continue;
    }

    const tokens = await resolveEnabledTokensForSharedWishlistReminder(recipientUids);

    if (tokens.length === 0) {
      logger.info("No valid tokens for shared wishlist reminder", {
        sharedWishlistId,
        recipients: recipientUids.length,
      });
      await storeReminderSent(reminderId, {
        entityId: sharedWishlistId,
        entityType: "shared-wishlist",
        reminderType: "deadline_minus_2d",
        sentAt: now,
      });
      continue;
    }

    const title = `<b>${escapeHtml(linkedWishlist?.title?.trim() || "Shared Wishlist")}</b> | Shared Wishlist`;
    const body = "Falten <b>2 dies</b> perquè finalitzi l'esdeveniment";
    const deeplink = `https://www.wishlify.com/shared-wishlist/${sharedWishlistId}`;

    const pushData: PushData = {
      type: "reminder",
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

    const response = await admin.messaging().sendEachForMulticast({
      tokens,
      data: pushData,
      android: {
        priority: "high",
      },
    });

    await storeReminderSent(reminderId, {
      entityId: sharedWishlistId,
      entityType: "shared-wishlist",
      reminderType: "deadline_minus_2d",
      sentAt: now,
    });

    logger.info("Shared wishlist deadline reminder processed", {
      sharedWishlistId,
      recipients: recipientUids.length,
      tokens: tokens.length,
      successCount: response.successCount,
      failureCount: response.failureCount,
    });
  }
}

async function sendSecretSantaDeadlineReminders(): Promise<void> {
  const now = Date.now();
  const secretSantaSnap = await db.collection("secret-santa").get();

  for (const secretSantaDoc of secretSantaSnap.docs) {
    const secretSanta = secretSantaDoc.data() as SecretSantaEntity;
    const secretSantaId = secretSantaDoc.id;

    const deadline = ensureOptionalNumber(secretSanta.deadline);
    if (!shouldSendDeadlineReminder(deadline, now)) {
      continue;
    }

    const reminderId = buildReminderId("secret-santa", secretSantaId);
    const alreadySent = await existsReminder(reminderId);
    if (alreadySent) {
      continue;
    }

    const recipientUids = await resolveSecretSantaReminderRecipients(secretSanta);

    if (recipientUids.length === 0) {
      logger.info("No recipients for secret santa reminder", {
        secretSantaId,
      });
      await storeReminderSent(reminderId, {
        entityId: secretSantaId,
        entityType: "secret-santa",
        reminderType: "deadline_minus_2d",
        sentAt: now,
      });
      continue;
    }

    const tokens = await resolveEnabledTokensForSecretSantaReminder(recipientUids);

    if (tokens.length === 0) {
      logger.info("No valid tokens for secret santa reminder", {
        secretSantaId,
        recipients: recipientUids.length,
      });
      await storeReminderSent(reminderId, {
        entityId: secretSantaId,
        entityType: "secret-santa",
        reminderType: "deadline_minus_2d",
        sentAt: now,
      });
      continue;
    }

    const title = `<b>${escapeHtml(secretSanta.name?.trim() || "Amic Invisible")}</b> | Amic Invisible`;
    const body = "Falten <b>2 dies</b> perquè finalitzi l'esdeveniment";
    const deeplink = `https://www.wishlify.com/secret-santa/${secretSantaId}`;

    const pushData: PushData = {
      type: "reminder",
      title,
      body,
      deeplink,
    };

    if (
      typeof secretSanta.photoUrl === "string" &&
      secretSanta.photoUrl.trim().length > 0
    ) {
      pushData.imageUrl = secretSanta.photoUrl.trim();
    }

    const response = await admin.messaging().sendEachForMulticast({
      tokens,
      data: pushData,
      android: {
        priority: "high",
      },
    });

    await storeReminderSent(reminderId, {
      entityId: secretSantaId,
      entityType: "secret-santa",
      reminderType: "deadline_minus_2d",
      sentAt: now,
    });

    logger.info("Secret santa deadline reminder processed", {
      secretSantaId,
      recipients: recipientUids.length,
      tokens: tokens.length,
      successCount: response.successCount,
      failureCount: response.failureCount,
    });
  }
}

function shouldSendDeadlineReminder(deadline: number | null, now: number): boolean {
  if (deadline == null) {
    return false;
  }

  const timeUntilDeadline = deadline - now;
  return timeUntilDeadline > 0 && timeUntilDeadline <= TWO_DAYS_MS;
}

function buildReminderId(
  entityType: "shared-wishlist" | "secret-santa",
  entityId: string
): string {
  return `${entityType}_${entityId}_deadline_minus_2d`;
}

async function existsReminder(reminderId: string): Promise<boolean> {
  const snap = await db.collection("system--sent-reminders").doc(reminderId).get();
  return snap.exists;
}

async function storeReminderSent(
  reminderId: string,
  data: {
    entityId: string;
    entityType: string;
    reminderType: string;
    sentAt: number;
  }
): Promise<void> {
  await db.collection("system--sent-reminders").doc(reminderId).set(data);
}

async function resolveSharedWishlistReminderRecipients(
  sharedWishlist: SharedWishlistEntity
): Promise<string[]> {
  const participants = ensureStringArray(sharedWishlist.participants);
  const groupMembers = await resolveGroupMembers(sharedWishlist.group);
  const editors =
    sharedWishlist.editorsCanSeeUpdates === true
      ? ensureStringArray(sharedWishlist.editors)
      : [];

  return Array.from(new Set([...participants, ...groupMembers, ...editors]));
}

async function resolveSecretSantaReminderRecipients(
  secretSanta: SecretSantaEntity
): Promise<string[]> {
  const participants = ensureStringArray(secretSanta.participants);
  const groupMembers = await resolveGroupMembers(secretSanta.group);
  const creator = ensureOptionalString(secretSanta.createdBy);

  return Array.from(
    new Set([...participants, ...groupMembers, ...(creator ? [creator] : [])])
  );
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

async function resolveEnabledTokensForSharedWishlistReminder(
  userIds: string[]
): Promise<string[]> {
  const userSnaps = await Promise.all(
    userIds.map((uid) => db.collection("users").doc(uid).get())
  );

  return userSnaps
    .filter((snap) => snap.exists)
    .map((snap) => snap.data() as UserEntity)
    .filter((user) => user.notifications?.sharedWishlistsDeadlineReminders === true)
    .map((user) => user.token)
    .filter((token): token is string => typeof token === "string" && token.trim().length > 0);
}

async function resolveEnabledTokensForSecretSantaReminder(
  userIds: string[]
): Promise<string[]> {
  const userSnaps = await Promise.all(
    userIds.map((uid) => db.collection("users").doc(uid).get())
  );

  return userSnaps
    .filter((snap) => snap.exists)
    .map((snap) => snap.data() as UserEntity)
    .filter((user) => user.notifications?.secretSantaDeadlineReminders === true)
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

function ensureOptionalNumber(value: unknown): number | null {
  return typeof value === "number" && Number.isFinite(value)
    ? value
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