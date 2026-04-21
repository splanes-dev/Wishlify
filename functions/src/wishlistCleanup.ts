import * as admin from "firebase-admin";
import * as functions from "firebase-functions/v1";

type WishlistItem = {
  id?: string;
  photoUrl?: string | null;
};

function itemImagePrefix(wishlistId: string, itemId: string): string {
  return `wishlists/${wishlistId}/items/${itemId}`;
}

export const cleanupWishlistOnDelete = functions
  .region("europe-west1")
  .runWith({ maxInstances: 10 })
  .firestore
  .document("wishlists/{wishlistId}")
  .onDelete(async (_snap, context) => {
    const db = admin.firestore();
    const bucket = admin.storage().bucket();

    const wishlistId = context.params.wishlistId as string;
    const itemsRef = db.collection(`wishlists/${wishlistId}/items`);

    const itemsSnap = await itemsRef.get();

    // Delete stored images
    await Promise.all(
      itemsSnap.docs.map(async (doc) => {
        const item = doc.data() as WishlistItem;
        const itemId = item.id ?? doc.id;

        if (!item.photoUrl) {
          return;
        }
        await bucket.deleteFiles({
          prefix: itemImagePrefix(wishlistId, itemId),
          force: true,
        });
      })
    );

    // Delete all subcollection
    await db.recursiveDelete(itemsRef);
  });