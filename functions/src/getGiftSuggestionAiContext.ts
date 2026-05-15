import { onCall, HttpsError } from "firebase-functions/v2/https";
import { getFirestore } from "firebase-admin/firestore";
import * as logger from "firebase-functions/logger";

type User = {
  uid?: string;
  username?: string;
  photoUrl?: string;
  hobbies?: {
    enabled?: boolean;
    values?: string[];
  };
};

type Group = {
  id?: string;
  members?: string[];
  createdBy?: string;
};

type Wishlist = {
  id?: string;
  title?: string;
  description?: string;
  photoUrl?: string;
  type?: "Own" | "ThirdParty";
  target?: string | null;
  shareStatus?: "Private" | "Shared";
  sharedWishlistId?: string | null;
  editors?: string[];
  createdBy?: string;
};

type WishlistItem = {
  id?: string;
  photoUrl?: string;
  name?: string;
  description?: string;
  store?: string;
  unitPrice?: number;
  amount?: number;
  priority?: number;
  link?: string;
  tags?: string[];
  createdBy?: string;
  purchased?: {
    purchasedAt?: number;
    purchasedBy?: string;
  } | null;
};

type SecretSanta = {
  name?: string;
  photoUrl?: string;
  budget?: number;
  deadline?: number;
  createdBy?: string;
  createdAt?: number;
  group?: string | null;
  participants?: string[];
  inviteLink?: string;
  exclusions?: Record<string, string[]>;
  drawStatus?: "Pending" | "Done";
};

type Assignment = {
  receiver?: string;
  giver?: string;
};

type GetGiftSuggestionAiContextRequest = {
  secretSantaId: string;
  targetUserId: string;
};

type GetGiftSuggestionAiContextResponse = {
  aiContext: string;
  budget: number | null;
  deadline: number | null;
  source: {
    hobbiesIncluded: boolean;
    wishlistsIncluded: number;
    itemsIncluded: number;
  };
};

type PublicProfileContextResult = {
  context: string;
  hobbiesIncluded: boolean;
};

type WishlistContextResult = {
  context: string;
  wishlistsIncluded: number;
  itemsIncluded: number;
};

const MAX_WISHLISTS = 4;
const MAX_ITEMS_PER_WISHLIST = 8;
const MAX_CONTEXT_LENGTH = 2000;
const MAX_DESCRIPTION_WORDS = 18;

export const getGiftSuggestionAiContext = onCall<
  GetGiftSuggestionAiContextRequest,
  Promise<GetGiftSuggestionAiContextResponse>
>(
  {
    region: "europe-west1",
    maxInstances: 1,
    timeoutSeconds: 15,
    memory: "256MiB",
  },
  async (request) => {
    const uid = request.auth?.uid;

    if (!uid) {
      throw new HttpsError("unauthenticated", "User must be authenticated");
    }

    const { secretSantaId, targetUserId } = request.data ?? {};

    if (!secretSantaId || typeof secretSantaId !== "string") {
      throw new HttpsError("invalid-argument", "secretSantaId is required");
    }

    if (!targetUserId || typeof targetUserId !== "string") {
      throw new HttpsError("invalid-argument", "targetUserId is required");
    }

    const db = getFirestore();

    const secretSantaRef = db.collection("secret-santa").doc(secretSantaId);
    const secretSantaSnap = await secretSantaRef.get();

    if (!secretSantaSnap.exists) {
      throw new HttpsError("not-found", "Secret Santa event not found");
    }

    const secretSanta = secretSantaSnap.data() as SecretSanta;

    if (secretSanta.drawStatus !== "Done") {
      throw new HttpsError(
        "failed-precondition",
        "Secret Santa draw has not been completed"
      );
    }

    const [currentUserHasAccess, targetUserHasAccess] = await Promise.all([
      isUserInSecretSantaContext(db, secretSanta, uid),
      isUserInSecretSantaContext(db, secretSanta, targetUserId),
    ]);

    if (!currentUserHasAccess) {
      throw new HttpsError(
        "permission-denied",
        "User is not allowed to access this Secret Santa event"
      );
    }

    if (!targetUserHasAccess) {
      throw new HttpsError(
        "permission-denied",
        "Target user is not part of this Secret Santa context"
      );
    }

    const assignmentSnap = await secretSantaRef
      .collection("assignments")
      .doc(uid)
      .get();

    if (!assignmentSnap.exists) {
      throw new HttpsError("not-found", "Assignment not found for current user");
    }

    const assignment = assignmentSnap.data() as Assignment;

    if (assignment.receiver !== targetUserId) {
      throw new HttpsError(
        "permission-denied",
        "User cannot access AI context for this target user"
      );
    }

    const publicProfileContext = await buildWeightedHobbiesContext(
      db,
      targetUserId
    );

    const wishlistContextResult = await buildCleanWishlistContext(
      db,
      targetUserId
    );

    const contextParts = [
      publicProfileContext.context,
      wishlistContextResult.context,
      secretSanta.budget != null
        ? `pressupost orientatiu: ${secretSanta.budget}`
        : "",
    ].filter(Boolean);

    const aiContext = truncateContext(contextParts.join(". "));

    logger.info("Generated gift suggestion AI context", {
      uid,
      secretSantaId,
      targetUserId,
      hobbiesIncluded: publicProfileContext.hobbiesIncluded,
      wishlistsIncluded: wishlistContextResult.wishlistsIncluded,
      itemsIncluded: wishlistContextResult.itemsIncluded,
      aiContextLength: aiContext.length,
    });

    return {
      aiContext,
      budget: secretSanta.budget ?? null,
      deadline: secretSanta.deadline ?? null,
      source: {
        hobbiesIncluded: publicProfileContext.hobbiesIncluded,
        wishlistsIncluded: wishlistContextResult.wishlistsIncluded,
        itemsIncluded: wishlistContextResult.itemsIncluded,
      },
    };
  }
);

async function isUserInSecretSantaContext(
  db: FirebaseFirestore.Firestore,
  secretSanta: SecretSanta,
  userId: string
): Promise<boolean> {
  if (secretSanta.createdBy === userId) {
    return true;
  }

  const participants = secretSanta.participants ?? [];

  if (participants.includes(userId)) {
    return true;
  }

  const groupId = secretSanta.group;

  if (!groupId) {
    return false;
  }

  return isUserGroupMember(db, groupId, userId);
}

async function isUserGroupMember(
  db: FirebaseFirestore.Firestore,
  groupId: string,
  userId: string
): Promise<boolean> {
  const groupSnap = await db.collection("groups").doc(groupId).get();

  if (!groupSnap.exists) {
    return false;
  }

  const group = groupSnap.data() as Group;
  const members = group.members ?? [];

  return members.includes(userId);
}

async function buildWeightedHobbiesContext(
  db: FirebaseFirestore.Firestore,
  targetUserId: string
): Promise<PublicProfileContextResult> {
  const userSnap = await db.collection("users").doc(targetUserId).get();

  if (!userSnap.exists) {
    return {
      context: "",
      hobbiesIncluded: false,
    };
  }

  const user = userSnap.data() as User;

  const hobbiesEnabled = user.hobbies?.enabled === true;
  const hobbies = normalizeStringArray(user.hobbies?.values);

  if (!hobbiesEnabled || hobbies.length === 0) {
    return {
      context: "",
      hobbiesIncluded: false,
    };
  }

  const hobbiesText = hobbies.join(", ");

  return {
    context: [
      `interessos principals: ${hobbiesText}`,
      `hobbies: ${hobbiesText}`,
      `interessos: ${hobbiesText}`,
    ].join(". "),
    hobbiesIncluded: true,
  };
}

async function buildCleanWishlistContext(
  db: FirebaseFirestore.Firestore,
  targetUserId: string
): Promise<WishlistContextResult> {
  const wishlistsSnap = await db
    .collection("wishlists")
    .where("editors", "array-contains", targetUserId)
    .limit(MAX_WISHLISTS)
    .get();

  const wishlistTitles: string[] = [];
  const wishlistDescriptions: string[] = [];
  const productNames: string[] = [];
  const tags: string[] = [];
  const descriptions: string[] = [];

  let wishlistsIncluded = 0;
  let itemsIncluded = 0;

  for (const wishlistDoc of wishlistsSnap.docs) {
    const wishlist = wishlistDoc.data() as Wishlist;

    if (!isWishlistRelevantForTarget(wishlist, targetUserId)) {
      continue;
    }

    const title = normalizeText(wishlist.title);
    const description = cleanDescription(wishlist.description);

    if (title && !isGenericWishlistTitle(title)) {
      wishlistTitles.push(title);
    }

    if (description) {
      wishlistDescriptions.push(description);
      descriptions.push(description);
    }

    const itemsSnap = await wishlistDoc.ref
      .collection("items")
      .limit(MAX_ITEMS_PER_WISHLIST)
      .get();

    let hasUsefulContent = Boolean(
      (title && !isGenericWishlistTitle(title)) || description
    );

    for (const itemDoc of itemsSnap.docs) {
      const item = itemDoc.data() as WishlistItem;

      const itemName = normalizeText(item.name);
      const itemDescription = cleanDescription(item.description);
      const itemTags = normalizeStringArray(item.tags);

      const hasUsefulItemContent =
        Boolean(itemName) || itemDescription.length > 0 || itemTags.length > 0;

      if (!hasUsefulItemContent) {
        continue;
      }

      if (
        itemName &&
        shouldIncludeProductName(itemName, itemTags, itemDescription)
      ) {
        productNames.push(itemName);
      }

      if (itemDescription) {
        descriptions.push(itemDescription);
      }

      tags.push(...itemTags);

      itemsIncluded++;
      hasUsefulContent = true;
    }

    if (hasUsefulContent) {
      wishlistsIncluded++;
    }
  }

  const uniqueTitles = unique(wishlistTitles).slice(0, 4);
  const uniqueDescriptions = unique(wishlistDescriptions).slice(0, 3);
  const uniqueProductNames = unique(productNames).slice(0, 18);
  const uniqueTags = unique(tags).slice(0, 18);
  const uniqueDescriptionsForContext = unique(descriptions).slice(0, 8);

  const contextParts = [
    uniqueTitles.length > 0
      ? `wishlist títols: ${uniqueTitles.join(", ")}`
      : "",
    uniqueDescriptions.length > 0
      ? `descripcions wishlist: ${uniqueDescriptions.join(", ")}`
      : "",
    uniqueProductNames.length > 0
      ? `wishlist productes: ${uniqueProductNames.join(", ")}`
      : "",
    uniqueTags.length > 0 ? `tags rellevants: ${uniqueTags.join(", ")}` : "",
    uniqueDescriptionsForContext.length > 0
      ? `descripcions productes: ${uniqueDescriptionsForContext.join(", ")}`
      : "",
  ].filter(Boolean);

  return {
    context: contextParts.join(". "),
    wishlistsIncluded,
    itemsIncluded,
  };
}

function isWishlistRelevantForTarget(
  wishlist: Wishlist,
  targetUserId: string
): boolean {
  const editors = wishlist.editors ?? [];
  return editors.includes(targetUserId);
}

function shouldIncludeProductName(
  name: string,
  tags: string[],
  description: string
): boolean {
  const meaningfulTokens = name
    .split(" ")
    .map((token) => token.trim())
    .filter((token) => token.length >= 3);

  if (meaningfulTokens.length >= 1) {
    return true;
  }

  return tags.length > 0 || description.length > 0;
}

function isGenericWishlistTitle(title: string): boolean {
  const normalized = normalizeText(title);

  const exactGenericTitles = [
    "idees regal",
    "ideas regalo",
    "wishlist",
    "llista",
    "lista",
    "cumple",
    "aniversari",
    "regals",
    "regalos",
    "diversio",
    "diversió",
  ];

  if (exactGenericTitles.includes(normalized)) {
    return true;
  }

  const genericWords = [
    "regal",
    "regals",
    "regalo",
    "regalos",
    "wishlist",
    "llista",
    "lista",
    "idees",
    "ideas",
    "cumple",
    "aniversari",
    "diversio",
    "diversió",
  ];

  const words = normalizeText(normalized)
    .split(" ")
    .filter(Boolean);

  return words.length > 0 && words.every((word) => genericWords.includes(word));
}

function cleanDescription(value: unknown): string {
  const text = normalizeText(value);

  if (!text) {
    return "";
  }

  return text.split(" ").slice(0, MAX_DESCRIPTION_WORDS).join(" ");
}

function normalizeStringArray(value: unknown): string[] {
  if (!Array.isArray(value)) {
    return [];
  }

  return unique(
    value
      .filter((item): item is string => typeof item === "string")
      .map((item) => normalizeText(item))
      .filter(Boolean)
  );
}

function normalizeText(value: unknown): string {
  if (typeof value !== "string") {
    return "";
  }

  return value
    .toLowerCase()
    .replace(/\n/g, " ")
    .replace(/[^\w\sàèéíïòóúüçñ·&]/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

function unique(values: string[]): string[] {
  return Array.from(new Set(values.filter(Boolean)));
}

function truncateContext(context: string): string {
  if (context.length <= MAX_CONTEXT_LENGTH) {
    return context;
  }

  return context.substring(0, MAX_CONTEXT_LENGTH).trim();
}