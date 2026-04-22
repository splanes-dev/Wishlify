import * as admin from "firebase-admin";
import { onSchedule } from "firebase-functions/v2/scheduler";

const db = admin.firestore();

const EXPIRATION_GRACE_PERIOD_MS = 30 * 24 * 60 * 60 * 1000;

type SharedWishlistEntity = {
  deadline?: number | null;
  wishlist?: string | null;
};

type WishlistEntity = {
  shareStatus?: string | null;
};

type SecretSantaEntity = {
  deadline?: number | null;
};

function isExpiredWithGracePeriod(deadline: unknown, now: number): boolean {
  return typeof deadline === "number" &&
    Number.isFinite(deadline) &&
    deadline + EXPIRATION_GRACE_PERIOD_MS < now;
}

async function cleanupExpiredSecretSantaEvents(): Promise<void> {
  const now = Date.now();
  const secretSantaSnap = await db.collection("secret-santa").get();

  for (const secretSantaDoc of secretSantaSnap.docs) {
    const secretSanta = secretSantaDoc.data() as SecretSantaEntity;

    if (!isExpiredWithGracePeriod(secretSanta.deadline, now)) {
      continue;
    }

    await db.recursiveDelete(secretSantaDoc.ref);
  }
}

async function cleanupExpiredSharedWishlists(): Promise<void> {
  const now = Date.now();
  const sharedWishlistsSnap = await db.collection("shared-wishlists").get();

  for (const sharedWishlistDoc of sharedWishlistsSnap.docs) {
    const sharedWishlist = sharedWishlistDoc.data() as SharedWishlistEntity;

    if (!isExpiredWithGracePeriod(sharedWishlist.deadline, now)) {
      continue;
    }

    const linkedWishlistId =
      typeof sharedWishlist.wishlist === "string" && sharedWishlist.wishlist.trim().length > 0
        ? sharedWishlist.wishlist
        : null;

    if (linkedWishlistId) {
      const wishlistRef = db.collection("wishlists").doc(linkedWishlistId);
      const wishlistSnap = await wishlistRef.get();

      if (wishlistSnap.exists) {
        const wishlist = wishlistSnap.data() as WishlistEntity | undefined;

        if (wishlist?.shareStatus === "Shared") {
          await db.recursiveDelete(wishlistRef);
        }
      }
    }

    await db.recursiveDelete(sharedWishlistDoc.ref);
  }
}

export const cleanupExpiredCollectionsDaily = onSchedule(
  {
    schedule: "0 0 * * *",
    timeZone: "Europe/Madrid",
    region: "europe-west1",
    maxInstances: 1,
  },
  async () => {
    await cleanupExpiredSecretSantaEvents();
    await cleanupExpiredSharedWishlists();
  }
);