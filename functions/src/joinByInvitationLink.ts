import * as admin from "firebase-admin";
import { onCall, HttpsError } from "firebase-functions/v2/https";

const db = admin.firestore();

type InvitationAction =
  | "wishlist_editor"
  | "shared_wishlist_participant"
  | "secret_santa_participant";

type JoinByInvitationRequest = {
  token?: string;
  actionId?: InvitationAction;
};

type JoinByInvitationResponse = {
  documentId: string;
  actionId: InvitationAction;
  alreadyMember: boolean;
};

export const joinByInvitationLink = onCall(
  {
    region: "europe-west1",
    maxInstances: 1,
    timeoutSeconds: 15,
    memory: "256MiB",
  },
  async (request): Promise<JoinByInvitationResponse> => {
    const uid = request.auth?.uid;
    const { token, actionId } = (request.data ?? {}) as JoinByInvitationRequest;

    if (!uid) {
      throw new HttpsError("unauthenticated", "User must be authenticated");
    }

    if (!token || typeof token !== "string") {
      throw new HttpsError("invalid-argument", "Token is required");
    }

    if (!actionId || !isValidAction(actionId)) {
      throw new HttpsError("invalid-argument", "Invalid actionId");
    }

    return db.runTransaction(async (tx) => {
      switch (actionId) {
        case "wishlist_editor":
          return joinWishlistAsEditor(tx, token, uid);

        case "shared_wishlist_participant":
          return joinSharedWishlistAsParticipant(tx, token, uid);

        case "secret_santa_participant":
          return joinSecretSantaAsParticipant(tx, token, uid);
      }
    });
  }
);

function isValidAction(value: string): value is InvitationAction {
  return (
    value === "wishlist_editor" ||
    value === "shared_wishlist_participant" ||
    value === "secret_santa_participant"
  );
}

async function findSingleDocByToken(
  tx: FirebaseFirestore.Transaction,
  collectionName: string,
  tokenField: string,
  token: string
): Promise<FirebaseFirestore.QueryDocumentSnapshot> {
  const query = db
    .collection(collectionName)
    .where(tokenField, "==", token)
    .limit(1);

  const snap = await tx.get(query);

  if (snap.empty) {
    throw new HttpsError("not-found", "Invitation not found");
  }

  return snap.docs[0]!;
}

function ensureStringArray(value: unknown): string[] {
  return Array.isArray(value)
    ? value.filter((v): v is string => typeof v === "string")
    : [];
}

function ensureOptionalString(value: unknown): string | null {
  return typeof value === "string" && value.trim().length > 0
    ? value
    : null;
}

function ensureOptionalNumber(value: unknown): number | null {
  return typeof value === "number" && Number.isFinite(value)
    ? value
    : null;
}

function hasDeadlineExpired(deadline: unknown): boolean {
  const deadlineMillis = ensureOptionalNumber(deadline);
  return deadlineMillis != null && deadlineMillis < Date.now();
}

async function isUserInGroup(
  tx: FirebaseFirestore.Transaction,
  groupId: string | null,
  uid: string
): Promise<boolean> {
  if (!groupId) return false;

  const groupRef = db.collection("groups").doc(groupId);
  const groupSnap = await tx.get(groupRef);

  if (!groupSnap.exists) return false;

  const members = ensureStringArray(groupSnap.data()?.members);
  return members.includes(uid);
}

async function joinWishlistAsEditor(
  tx: FirebaseFirestore.Transaction,
  token: string,
  uid: string
): Promise<JoinByInvitationResponse> {
  const doc = await findSingleDocByToken(tx, "wishlists", "editorInviteLink", token);
  const data = doc.data();

  const editors = ensureStringArray(data.editors);

  if (editors.includes(uid)) {
    return {
      documentId: doc.id,
      actionId: "wishlist_editor",
      alreadyMember: true,
    };
  }

  tx.update(doc.ref, {
    editors: admin.firestore.FieldValue.arrayUnion(uid),
  });

  return {
    documentId: doc.id,
    actionId: "wishlist_editor",
    alreadyMember: false,
  };
}

async function joinSharedWishlistAsParticipant(
  tx: FirebaseFirestore.Transaction,
  token: string,
  uid: string
): Promise<JoinByInvitationResponse> {
  const doc = await findSingleDocByToken(
    tx,
    "shared-wishlists",
    "inviteLink",
    token
  );
  const data = doc.data();

  if (hasDeadlineExpired(data.deadline)) {
    throw new HttpsError(
      "failed-precondition",
      "Shared wishlist invitation expired"
    );
  }

  const participants = ensureStringArray(data.participants);
  const groupId = ensureOptionalString(data.group);

  if (participants.includes(uid)) {
    return {
      documentId: doc.id,
      actionId: "shared_wishlist_participant",
      alreadyMember: true,
    };
  }

  const belongsToGroup = await isUserInGroup(tx, groupId, uid);
  if (belongsToGroup) {
    throw new HttpsError(
      "failed-precondition",
      "User already belongs to the linked group"
    );
  }

  tx.update(doc.ref, {
    participants: admin.firestore.FieldValue.arrayUnion(uid),
  });

  return {
    documentId: doc.id,
    actionId: "shared_wishlist_participant",
    alreadyMember: false,
  };
}

async function joinSecretSantaAsParticipant(
  tx: FirebaseFirestore.Transaction,
  token: string,
  uid: string
): Promise<JoinByInvitationResponse> {
  const doc = await findSingleDocByToken(
    tx,
    "secret-santa",
    "inviteLink",
    token
  );
  const data = doc.data();

  if (hasDeadlineExpired(data.deadline)) {
    throw new HttpsError(
      "failed-precondition",
      "Secret Santa invitation expired"
    );
  }

  if (data.drawStatus === "Done") {
    throw new HttpsError(
      "failed-precondition",
      "Secret Santa draw already done"
    );
  }

  const participants = ensureStringArray(data.participants);
  const groupId = ensureOptionalString(data.group);

  if (participants.includes(uid)) {
    return {
      documentId: doc.id,
      actionId: "secret_santa_participant",
      alreadyMember: true,
    };
  }

  const belongsToGroup = await isUserInGroup(tx, groupId, uid);
  if (belongsToGroup) {
    throw new HttpsError(
      "failed-precondition",
      "User already belongs to the linked group"
    );
  }

  tx.update(doc.ref, {
    participants: admin.firestore.FieldValue.arrayUnion(uid),
  });

  return {
    documentId: doc.id,
    actionId: "secret_santa_participant",
    alreadyMember: false,
  };
}