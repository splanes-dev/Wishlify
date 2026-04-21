import * as admin from "firebase-admin";

admin.initializeApp();

export {createUidByMailIndex} from "./auth";
export {cleanupWishlistOnDelete} from "./wishlistCleanup";
export {expireSharedWishlistStatesDaily} from "./scheduledSharedWishlistCleanup";
export {extractLinkMetadata} from "./extractLinkMetadata";
export {joinByInvitationLink} from "./joinByInvitationLink";