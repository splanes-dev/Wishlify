import * as admin from "firebase-admin";
import * as crypto from "crypto";
import * as functions from "firebase-functions/v1";

function normalizeEmail(email: string): string {
  return email.trim().toLowerCase();
}

function hashEmail(email: string): string {
  return crypto
    .createHash("sha256")
    .update(email)
    .digest("hex");
}

export const createUidByMailIndex = functions
  .region("europe-west1")
  .runWith({ maxInstances: 10 })
  .auth
  .user()
  .onCreate(
    async (user) => {
      const email = user.email;

      if (!email) {
        console.log(
          `User ${user.uid} created without email. Skipping index creation.`
        );
        return;
      }

      const normalizedEmail = normalizeEmail(email);
      const emailHash = hashEmail(normalizedEmail);

      await admin
        .firestore()
        .doc(`system--uid-by-mail/${emailHash}`)
        .set({
          uid: user.uid,
        });

      console.log(`Indexed user ${user.uid} under hash ${emailHash}`);
    }
);