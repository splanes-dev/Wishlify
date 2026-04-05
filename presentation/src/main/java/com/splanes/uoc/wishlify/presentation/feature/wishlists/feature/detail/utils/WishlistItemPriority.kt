package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HotelClass
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun WishlistItem.Priority.name() = when (this) {
  WishlistItem.Priority.Standard -> R.string.wishlists_priority_standard
  WishlistItem.Priority.Top -> R.string.wishlists_priority_top
  WishlistItem.Priority.Supertop -> R.string.wishlists_priority_supertop
}.let { id -> stringResource(id) }

@Composable
fun WishlistItem.Priority.icon() = when (this) {
  WishlistItem.Priority.Standard -> Icons.Rounded.ArrowUpward
  WishlistItem.Priority.Top -> Icons.Outlined.HotelClass
  WishlistItem.Priority.Supertop -> Icons.Outlined.LocalFireDepartment
}

@Composable
fun WishlistItem.Priority.color() = when (this) {
  WishlistItem.Priority.Standard -> WishlifyTheme.colorScheme.onSurfaceVariant
  WishlistItem.Priority.Top -> WishlifyTheme.colorScheme.info
  WishlistItem.Priority.Supertop -> WishlifyTheme.colorScheme.error
}