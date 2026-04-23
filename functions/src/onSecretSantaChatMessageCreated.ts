import * as admin from "firebase-admin";
import {onDocumentCreated} from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";

const db = admin.firestore();

type SecretSantaEntity = {
  id?: string;
  name?: string;
  photoUrl?: string | null;
};

type SecretSantaChatEntity = {
  id?: string;
  type?: "receiver" | "giver";
  receiver?: string;
  giver?: string;
  createdAt?: number;
};

type SecretSantaChatMessageEntity = {
  id?: string;
  sender?: string;
  text?: string;
  createdAt?: number;
};

type UserEntity = {
  username?: string;
  token?: string | null;
  notifications?: {
    secretSantaChat?: boolean;
  };
};

type PushData = Record<string, string>;

const CHAT_MESSAGE_MAX_LENGTH = 80;

export const onSecretSantaChatMessageCreated = onDocumentCreated(
  {
    document: "secret-santa/{secretSantaId}/chats/{chatId}/messages/{messageId}",
    region: "europe-west1",
    maxInstances: 1,
  },
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) {
      logger.warn("Missing snapshot in onSecretSantaChatMessageCreated");
      return;
    }

    const {secretSantaId, chatId} = event.params;
    const message = snapshot.data() as SecretSantaChatMessageEntity;

    const senderUid = message.sender;
    if (!senderUid) {
      logger.warn("Secret santa chat message without sender", {
        secretSantaId,
        chatId,
        messageId: snapshot.id,
      });
      return;
    }

    const chatSnap = await db
      .collection("secret-santa")
      .doc(secretSantaId)
      .collection("chats")
      .doc(chatId)
      .get();

    if (!chatSnap.exists) {
      logger.warn("Secret santa chat not found for notification", {
        secretSantaId,
        chatId,
        messageId: snapshot.id,
      });
      return;
    }

    const chat = chatSnap.data() as SecretSantaChatEntity;
    const recipientUid = resolveRecipientUid(chat, senderUid);

    if (!recipientUid) {
      logger.warn("Unable to resolve recipient for secret santa chat notification", {
        secretSantaId,
        chatId,
        messageId: snapshot.id,
        senderUid,
      });
      return;
    }

    if (recipientUid === senderUid) {
      logger.warn("Secret santa chat recipient equals sender, skipping push", {
        secretSantaId,
        chatId,
        messageId: snapshot.id,
        senderUid,
      });
      return;
    }

    const recipient = await resolveUser(recipientUid);

    if (!recipient) {
      logger.warn("Recipient user not found for secret santa chat notification", {
        secretSantaId,
        chatId,
        messageId: snapshot.id,
        recipientUid,
      });
      return;
    }

    if (recipient.notifications?.secretSantaChat !== true) {
      logger.info("Recipient has secret santa chat notifications disabled", {
        secretSantaId,
        chatId,
        messageId: snapshot.id,
        recipientUid,
      });
      return;
    }

    if (!recipient.token || !recipient.token.trim()) {
      logger.info("Recipient has no valid token for secret santa chat notification", {
        secretSantaId,
        chatId,
        messageId: snapshot.id,
        recipientUid,
      });
      return;
    }

    const secretSantaSnap = await db
      .collection("secret-santa")
      .doc(secretSantaId)
      .get();

    if (!secretSantaSnap.exists) {
      logger.warn("Secret santa event not found for chat notification", {
        secretSantaId,
        chatId,
        messageId: snapshot.id,
      });
      return;
    }

    const secretSanta = secretSantaSnap.data() as SecretSantaEntity;

    const senderDisplayName = await resolveSenderDisplayName({
      senderUid,
      recipientUid,
      chat,
    });

    const escapedEventTitle = escapeHtml(
      secretSanta.name?.trim() || "Amic Invisible"
    );
    const escapedSenderName = escapeHtml(senderDisplayName);
    const escapedMessageText = escapeHtml(
      truncateText(message.text?.trim() || "Nou missatge", CHAT_MESSAGE_MAX_LENGTH)
    );
    const giver = ensureOptionalString(chat.giver);
    const receiver = ensureOptionalString(chat.receiver);

    if (!giver || !receiver) {
      logger.warn("Invalid chat participants for deeplink generation", {
        secretSantaId,
        chatId,
        messageId: snapshot.id,
      });
      return;
    }


    const title = `<b>${escapedEventTitle}</b> | Amic Invisible`;
    const body = `<b>${escapedSenderName}</b>: ${escapedMessageText}`;
    const recipientIsGiver = recipientUid === giver;
    const chatPerspective = recipientIsGiver ? "as_giver" : "as_receiver";
    const deeplink =
      `https://www.wishlify.com/secret-santa/${secretSantaId}/chat` +
      `?perspective=${chatPerspective}`;

    const pushData: PushData = {
      type: "chat",
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

    const response = await admin.messaging().send({
      token: recipient.token.trim(),
      data: pushData,
      android: {
        priority: "high",
      },
    });

    logger.info("Secret santa chat push processed", {
      secretSantaId,
      chatId,
      messageId: snapshot.id,
      senderUid,
      recipientUid,
      response,
    });
  }
);

function resolveRecipientUid(
  chat: SecretSantaChatEntity,
  senderUid: string
): string | null {
  const giver = ensureOptionalString(chat.giver);
  const receiver = ensureOptionalString(chat.receiver);

  if (!giver || !receiver) {
    return null;
  }

  if (senderUid === giver) {
    return receiver;
  }

  if (senderUid === receiver) {
    return giver;
  }

  return null;
}

async function resolveSenderDisplayName(params: {
  senderUid: string;
  recipientUid: string;
  chat: SecretSantaChatEntity;
}): Promise<string> {
  const {senderUid, recipientUid, chat} = params;

  const giver = ensureOptionalString(chat.giver);
  const receiver = ensureOptionalString(chat.receiver);

  if (!giver || !receiver) {
    return "Anònim";
  }

  const recipientIsGiver = recipientUid === giver;

  if (!recipientIsGiver) {
    return "Anònim";
  }

  const senderName = await resolveUsername(senderUid);
  return senderName ?? "Algú";
}

async function resolveUser(uid: string): Promise<UserEntity | null> {
  const userSnap = await db.collection("users").doc(uid).get();
  if (!userSnap.exists) {
    return null;
  }

  return userSnap.data() as UserEntity;
}

async function resolveUsername(uid: string): Promise<string | null> {
  const user = await resolveUser(uid);
  return typeof user?.username === "string" && user.username.trim().length > 0 ?
    user.username :
    null;
}

function ensureOptionalString(value: unknown): string | null {
  return typeof value === "string" && value.trim().length > 0 ?
    value.trim() :
    null;
}

function truncateText(value: string, maxLength: number): string {
  return value.length <= maxLength ?
    value :
    `${value.slice(0, maxLength - 3).trimEnd()}...`;
}

function escapeHtml(value: string): string {
  return value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}
