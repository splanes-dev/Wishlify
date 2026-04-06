package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.mapper

import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.ShareWishlistRequest

class WishlistShareUiMapper {

  fun requestOf(
    wishlist: Wishlist,
    group: Group?,
    inviteLink: InviteLink,
    editorsCanSeeUpdates: Boolean,
    deadline: Long
  ): ShareWishlistRequest =
    ShareWishlistRequest(
      wishlistId = wishlist.id,
      owner = wishlist.createdBy.uid,
      editors = wishlist.editors.map { it.uid },
      group = group?.id,
      shareLink = inviteLink,
      editorsCanSeeUpdates = editorsCanSeeUpdates,
      deadline = deadline,
    )
}