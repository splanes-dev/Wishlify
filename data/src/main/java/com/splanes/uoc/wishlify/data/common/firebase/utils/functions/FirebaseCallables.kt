package com.splanes.uoc.wishlify.data.common.firebase.utils.functions

import com.google.firebase.functions.FirebaseFunctions

/** Invokes the callable function that extracts product metadata from a URL. */
fun FirebaseFunctions.extractLinkMetadata(url: String) =
  getHttpsCallable(Callables.EXTRACT_LINK_METADATA).call(mapOf("url" to url))

/** Invokes the callable function that joins an invitation-based collaborative flow. */
fun FirebaseFunctions.joinByInvitationLink(token: String, type: JoinByInvitationLinkType) =
  getHttpsCallable(Callables.JOIN_BY_INVITATION_LINK)
    .call(mapOf("token" to token, "actionId" to type.value))


/** Supported invitation actions handled by the shared join callable. */
enum class JoinByInvitationLinkType(val value: String) {
  WishlistEditor("wishlist_editor"),
  SharedWishlist("shared_wishlist_participant"),
  SecretSanta("secret_santa_participant")
}

private object Callables {
  const val EXTRACT_LINK_METADATA = "extractLinkMetadata"
  const val JOIN_BY_INVITATION_LINK = "joinByInvitationLink"
}
