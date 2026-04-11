import * as admin from "firebase-admin";
import {onSchedule} from "firebase-functions/v2/scheduler";

const db = admin.firestore();

type SharedWishlistItemEntity = {
  state?: string;
  reservation?: {
    expiresAt?: number | null;
  } | null;
  shareRequest?: {
    expiresAt?: number | null;
  } | null;
};

async function cleanupExpiredItemsByState(
  state: "Reserved" | "ShareRequest"
): Promise<void> {
  const sharedWishlistsSnap = await db.collection("shared-wishlists").get();
  const now = Date.now();

  for (const sharedWishlistDoc of sharedWishlistsSnap.docs) {
    const itemsRef = db.collection(
      `shared-wishlists/${sharedWishlistDoc.id}/items`
    );

    const itemsSnap = await itemsRef.where("state", "==", state).get();

    if (itemsSnap.empty) {
      continue;
    }

    const batch = db.batch();
    let hasWrites = false;

    for (const itemDoc of itemsSnap.docs) {
      const item = itemDoc.data() as SharedWishlistItemEntity;

      const expiresAt =
        state === "Reserved"
          ? item.reservation?.expiresAt
          : item.shareRequest?.expiresAt;

      if (expiresAt != null && expiresAt <= now) {
        batch.update(itemDoc.ref, {
          state: "Available",
          reservation: null,
          shareRequest: null,
          purchased: null,
        });
        hasWrites = true;
      }
    }

    if (hasWrites) {
      await batch.commit();
    }
  }
}

export const expireSharedWishlistStatesDaily = onSchedule(
  {
    schedule: "0 0 * * *",
    timeZone: "Europe/Madrid",
    region: "europe-west1",
    maxInstances: 1,
  },
  async () => {
    await cleanupExpiredItemsByState("Reserved");
    await cleanupExpiredItemsByState("ShareRequest");
  }
);