package com.splanes.uoc.wishlify.data.common.firebase.utils.functions

import com.google.firebase.functions.FirebaseFunctions

fun FirebaseFunctions.extractLinkMetadata(url: String) =
  getHttpsCallable(Callables.EXTRACT_LINK_METADATA).call(mapOf("url" to url))

fun FirebaseFunctions.joinByInvitationLink(token: String, type: JoinByInvitationLinkType) =
  getHttpsCallable(Callables.JOIN_BY_INVITATION_LINK)
    .call(mapOf("token" to token, "actionId" to type.value))


enum class JoinByInvitationLinkType(val value: String) {
  WishlistEditor("wishlist_editor"),
  SharedWishlist("shared_wishlist_participant"),
  SecretSanta("secret_santa_participant")
}

private object Callables {
  const val EXTRACT_LINK_METADATA = "extractLinkMetadata"
  const val JOIN_BY_INVITATION_LINK = "joinByInvitationLink"
}