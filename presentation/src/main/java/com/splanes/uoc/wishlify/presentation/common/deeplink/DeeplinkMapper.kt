package com.splanes.uoc.wishlify.presentation.common.deeplink

import android.net.Uri
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.JoinSecretSanta
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.JoinSecretSantaPattern
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.JoinSharedWishlist
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.JoinSharedWishlistPattern
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.JoinWishlistEditor
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.JoinWishlistEditorPattern
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SecretSantaChat
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SecretSantaChatPattern
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SharedWishlistChat
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SharedWishlistChatPattern

class DeeplinkMapper {

  fun map(uri: Uri): Deeplink? {
    val uriString = uri.path.orEmpty()
    val match = Deeplink.find(uriString)

    return when (match) {
      JoinSecretSantaPattern -> {
        uri.lastPathSegment?.let { token ->
          JoinSecretSanta(token = token)
        }
      }

      JoinSharedWishlistPattern -> {
        uri.lastPathSegment?.let { token ->
          JoinSharedWishlist(token = token)
        }
      }

      JoinWishlistEditorPattern -> {
        uri.lastPathSegment?.let { token ->
          JoinWishlistEditor(token = token)
        }
      }

      SharedWishlistChatPattern -> {
        val sharedWishlistId = uri.pathSegments.dropLast(1).lastOrNull()
        sharedWishlistId?.let { id -> SharedWishlistChat(id) }
      }

      SecretSantaChatPattern -> {
        val chatType = uri.getQueryParameter("perspective")
        val secretSantaId = uri.pathSegments.dropLast(1).lastOrNull()
        if (secretSantaId != null && chatType != null) {
          SecretSantaChat(secretSantaId, chatType)
        } else {
          null
        }
      }

      null -> null

    }
  }
}