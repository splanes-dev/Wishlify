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
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SecretSantaDetail
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SecretSantaDetailPattern
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SharedWishlistChat
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SharedWishlistChatPattern
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SharedWishlistDetail
import com.splanes.uoc.wishlify.presentation.common.deeplink.Deeplink.SharedWishlistDetailPattern

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

      SharedWishlistDetailPattern -> {
        val sharedWishlistId = uri.lastPathSegment
        sharedWishlistId?.let { id -> SharedWishlistDetail(id) }
      }

      SharedWishlistChatPattern -> {
        val sharedWishlistId = uri.pathSegments.dropLast(1).lastOrNull()
        sharedWishlistId?.let { id -> SharedWishlistChat(id) }
      }

      SecretSantaDetailPattern -> {
        val secretSantaId = uri.lastPathSegment
        secretSantaId?.let { id -> SecretSantaDetail(id) }
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