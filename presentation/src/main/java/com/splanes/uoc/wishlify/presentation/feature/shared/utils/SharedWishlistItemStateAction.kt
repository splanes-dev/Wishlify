package com.splanes.uoc.wishlify.presentation.feature.shared.utils

import android.content.res.Resources
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistItemStateAction
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SharedWishlistItemStateAction.icon() =
  when (this) {
    SharedWishlistItemStateAction.Purchase -> rememberVectorPainter(Icons.Outlined.Verified)
    SharedWishlistItemStateAction.Lock -> rememberVectorPainter(Icons.Outlined.LockClock)
    SharedWishlistItemStateAction.RequestShare -> painterResource(R.drawable.ic_share_request)
    SharedWishlistItemStateAction.Unlock -> rememberVectorPainter(Icons.Outlined.LockOpen)
    SharedWishlistItemStateAction.JoinToShareRequest -> rememberVectorPainter(Icons.Outlined.GroupAdd)
    SharedWishlistItemStateAction.CancelShareRequest -> painterResource(R.drawable.ic_share_off)
  }

@Composable
fun SharedWishlistItemStateAction.name() =
  when (this) {
    SharedWishlistItemStateAction.Purchase -> R.string.shared_wishlists_item_state_selector_purchased
    SharedWishlistItemStateAction.Lock -> R.string.shared_wishlists_item_state_selector_lock
    SharedWishlistItemStateAction.RequestShare -> R.string.shared_wishlists_item_state_selector_request_share
    SharedWishlistItemStateAction.Unlock -> R.string.shared_wishlists_item_state_selector_unlock
    SharedWishlistItemStateAction.JoinToShareRequest -> R.string.shared_wishlists_item_state_selector_join_share_request
    SharedWishlistItemStateAction.CancelShareRequest -> R.string.shared_wishlists_item_state_selector_delete_share_request
  }.let { id -> stringResource(id) }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedWishlistItemStateAction.colors() =
  with(WishlifyTheme.colorScheme) {
    when (this@colors) {
      SharedWishlistItemStateAction.Purchase -> successContainer to success
      SharedWishlistItemStateAction.Lock -> warningContainer to warning
      SharedWishlistItemStateAction.RequestShare -> infoContainer to info
      SharedWishlistItemStateAction.Unlock -> errorContainer to error
      SharedWishlistItemStateAction.JoinToShareRequest -> infoContainer to info
      SharedWishlistItemStateAction.CancelShareRequest -> errorContainer to error
    }.let { (containerColor, contentColor) ->
      ToggleButtonDefaults.toggleButtonColors(
        containerColor = Color.Transparent,
        contentColor = contentColor,
        checkedContainerColor = containerColor,
        checkedContentColor = contentColor
      )
    }
  }

@Composable
fun SharedWishlistItemStateAction.border() =
  with(WishlifyTheme.colorScheme) {
    when (this@border) {
      SharedWishlistItemStateAction.Purchase -> success
      SharedWishlistItemStateAction.Lock -> warning
      SharedWishlistItemStateAction.RequestShare -> info
      SharedWishlistItemStateAction.Unlock -> error
      SharedWishlistItemStateAction.JoinToShareRequest -> info
      SharedWishlistItemStateAction.CancelShareRequest -> error
    }.let { color -> BorderStroke(1.dp, color) }
  }

fun SharedWishlistItemStateAction.supportingText(resources: Resources): AnnotatedString? =
  when (this) {
    SharedWishlistItemStateAction.Lock -> R.string.shared_wishlists_item_state_selector_lock_supporting_text
    SharedWishlistItemStateAction.RequestShare -> R.string.shared_wishlists_item_state_selector_request_share_supporting_text
    SharedWishlistItemStateAction.Purchase,
    SharedWishlistItemStateAction.Unlock,
    SharedWishlistItemStateAction.JoinToShareRequest,
    SharedWishlistItemStateAction.CancelShareRequest -> null
  }?.let { id -> AnnotatedString.fromHtml(resources.getString(id)) }