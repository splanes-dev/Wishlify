package com.splanes.uoc.wishlify.presentation.feature.shared.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.utils.formatted
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.common.utils.joinToStringLast
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun SharedWishlistItem.State.icon() =
  when (this) {
    SharedWishlistItem.Available -> error("No icon() available for state=Available")
    is SharedWishlistItem.Lock -> rememberVectorPainter(Icons.Outlined.LockClock)
    is SharedWishlistItem.Purchased -> rememberVectorPainter(Icons.Outlined.Verified)
    is SharedWishlistItem.ShareRequest -> painterResource(R.drawable.ic_share_request)
  }

@Composable
fun SharedWishlistItem.State.name() =
  when (this) {
    SharedWishlistItem.Available -> error("No name() available for state=Available")
    is SharedWishlistItem.Lock -> R.string.shared_wishlists_item_state_lock
    is SharedWishlistItem.Purchased -> R.string.shared_wishlists_item_state_purchased
    is SharedWishlistItem.ShareRequest -> R.string.shared_wishlists_item_state_share_request
  }.let { id -> stringResource(id) }

@Composable
fun SharedWishlistItem.State.containerColor() =
  when (this) {
    SharedWishlistItem.Available -> error("No containerColor() available for state=Available")
    is SharedWishlistItem.Lock -> WishlifyTheme.colorScheme.warningContainer
    is SharedWishlistItem.Purchased -> WishlifyTheme.colorScheme.successContainer
    is SharedWishlistItem.ShareRequest -> WishlifyTheme.colorScheme.infoContainer
  }

@Composable
fun SharedWishlistItem.State.contentColor() =
  when (this) {
    SharedWishlistItem.Available -> error("No contentColor() available for state=Available")
    is SharedWishlistItem.Lock -> WishlifyTheme.colorScheme.onWarningContainer
    is SharedWishlistItem.Purchased -> WishlifyTheme.colorScheme.onSuccessContainer
    is SharedWishlistItem.ShareRequest -> WishlifyTheme.colorScheme.onInfoContainer
  }

@Composable
fun SharedWishlistItem.State.descriptionText() =
  when (this) {
    SharedWishlistItem.Available ->
      stringResource(R.string.shared_wishlists_item_state_available_description_text)
        .let(::AnnotatedString)

    is SharedWishlistItem.Lock ->
      htmlString(
        R.string.shared_wishlists_item_state_lock_description_text,
        reservedByGroup
          .map { it.username }
          .joinToStringLast(lastSeparator = " ${stringResource(R.string.and)} "),
        reservedAt.formatted(),
        expiresAt.formatted()
      )

    is SharedWishlistItem.Purchased ->
      htmlString(
        R.string.shared_wishlists_item_state_purchased_description_text,
        purchasedByGroup
          .map { it.username }
          .joinToStringLast(lastSeparator = " ${stringResource(R.string.and)} "),
        purchasedAt.formatted(),
      )

    is SharedWishlistItem.ShareRequest ->
      htmlString(
        R.string.shared_wishlists_item_state_share_request_description_text,
        requestedBy.username,
        numOfParticipantsRequested - participantsJoined.count(),
        numOfParticipantsRequested + 1,
        expiresAt.formatted()
      )
  }