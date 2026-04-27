package com.splanes.uoc.wishlify.domain.common.model

import android.net.Uri
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import kotlin.uuid.ExperimentalUuidApi

/**
 * Domain model for invitation links that allow users to join a collaborative flow.
 *
 * The link is defined by a generated token and the feature origin that will
 * consume it.
 */
data class InviteLink(
  val token: String,
  val origin: Origin,
) {

  /**
   * Builds the public Wishlify URL associated with this invitation.
   */
  fun asUrl(): String =
    Uri.Builder()
      .scheme(SCHEME)
      .authority(AUTHORITY)
      .appendPath(origin.path)
      .appendPath("join")
      .appendPath(token)
      .build()
      .toString()

  /**
   * Feature origin that determines which route will handle the invitation.
   */
  enum class Origin(val path: String) {
    WishlistEditor("wishlist"),
    WishlistShare("shared-wishlist"),
    SecretSanta("secret-santa");

    companion object
  }

  companion object {

    val WishlistsEditor = Origin.WishlistEditor
    val WishlistShare = Origin.WishlistShare
    val SecretSanta = Origin.SecretSanta

    /**
     * Creates a new invitation link with a fresh token for the given [origin].
     */
    @OptIn(ExperimentalUuidApi::class)
    fun new(origin: Origin) =
      InviteLink(
        token = newUuid(),
        origin = origin
      )
  }
}

private const val SCHEME = "https"
private const val AUTHORITY = "www.wishlify.com"

private fun InviteLink.Origin.Companion.from(path: String): InviteLink.Origin? =
  InviteLink.Origin.entries.find { o -> o.path.equals(path, ignoreCase = true) }
