package com.splanes.uoc.wishlify.domain.common.model

import android.net.Uri
import androidx.core.net.toUri
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
      .appendPath(token)
      .build()
      .toString()

  enum class Origin(val path: String) {
    WishlistEditor("wishlists-editor"),
    WishlistShare("wishlist-share");

    companion object
  }

  companion object {

    val WishlistsEditor = Origin.WishlistEditor
    val WishlistShare = Origin.WishlistShare

    fun fromUrl(url: String): InviteLink? =
      runCatching {
        val uri = url.toUri()
        when {
          uri.scheme != SCHEME -> null
          uri.authority != AUTHORITY -> null
          uri.pathSegments.count() != 2 -> null
          else -> {
            val (originString, token) = uri.pathSegments
            val origin = Origin.from(originString)
            when {
              origin == null -> null
              token.isEmpty() -> null
              else -> {
                InviteLink(
                  token = token,
                  origin = origin
                )
              }
            }
          }
        }
      }.getOrNull()

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