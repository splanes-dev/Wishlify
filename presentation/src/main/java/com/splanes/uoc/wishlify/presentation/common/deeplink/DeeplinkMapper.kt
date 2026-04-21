package com.splanes.uoc.wishlify.presentation.common.deeplink

import android.net.Uri
import com.splanes.uoc.wishlify.domain.common.model.InviteLink

class DeeplinkMapper {

  fun map(uri: Uri): Deeplink? {
    val token = uri.lastPathSegment ?: return null

    return when (uri.pathSegments.firstOrNull()) {
      InviteLink.WishlistsEditor.path ->
        Deeplink.WishlistEditor(token = token)

      InviteLink.WishlistShare.path ->
        Deeplink.WishlistShare(token = token)

      InviteLink.SecretSanta.path ->
        Deeplink.SecretSanta(token = token)

      else -> null
    }
  }
}