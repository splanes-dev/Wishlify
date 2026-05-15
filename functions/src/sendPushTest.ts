import * as admin from "firebase-admin";
import { onRequest } from "firebase-functions/v2/https";

type PushType = "chat" | "reminder" | "update";

export const sendTestPush = onRequest(
  {
    region: "europe-west1",
    maxInstances: 1,
    timeoutSeconds: 15,
    memory: "256MiB",
  },
  async (req, res) => {
    try {
      const token =
        (req.method === "GET" ? req.query.token : req.body?.token) as string | undefined;

      const type =
        ((req.method === "GET" ? req.query.type : req.body?.type) as PushType | undefined) ??
        "chat";

      const deeplink =
        (req.method === "GET" ? req.query.deeplink : req.body?.deeplink) as string | undefined;

      const title =
        (req.method === "GET" ? req.query.title : req.body?.title) as string | undefined;

      const body =
        (req.method === "GET" ? req.query.body : req.body?.body) as string | undefined;

      if (!token) {
        res.status(400).json({ error: "Missing token" });
        return;
      }

      const resolvedTitle =
        title ??
        (type === "chat"
          ? "Test chat"
          : type === "reminder"
            ? "Test reminder"
            : "Test update");

      const resolvedBody =
        body ??
        (type === "chat"
          ? "Això és una notificació de prova de xat"
          : type === "reminder"
            ? "Això és un recordatori de prova"
            : "Això és una actualització de prova");

      const resolvedDeeplink =
        deeplink ??
        (type === "chat"
          ? "https://www.wishlify.com/shared-wishlist/test/chat"
          : type === "reminder"
            ? "https://www.wishlify.com/shared-wishlist/test"
            : "https://www.wishlify.com/shared-wishlist/test");

      const response = await admin.messaging().send({
        token,
        data: {
          type,
          title: resolvedTitle,
          body: resolvedBody,
          deeplink: resolvedDeeplink,
        },
        android: {
          priority: "high",
        },
      });

      res.status(200).json({
        ok: true,
        messageId: response,
      });
    } catch (error) {
      console.error("sendTestPush failed", error);
      res.status(500).json({
        ok: false,
        error: error instanceof Error ? error.message : "Unknown error",
      });
    }
  }
);