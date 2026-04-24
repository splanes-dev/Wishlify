package com.splanes.uoc.wishlify.domain.common.model

import android.net.Uri
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import kotlin.uuid.ExperimentalUuidApi

data class InviteLink(
  val token: String,
  val origin: Origin,
) {

  fun asUrl(): String =
    Uri.Builder()
      .scheme(SCHEME)
      .authority(AUTHORITY)
      .appendPath(origin.path)
      .appendPath("join")
      .appendPath(token)
      .build()
      .toString()

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