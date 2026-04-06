package com.splanes.uoc.wishlify.data.feature.shared.mapper

import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistEntity
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.ShareWishlistRequest

class SharedWishlistsDataMapper {

  fun sharedWishlistFromRequest(request: ShareWishlistRequest): SharedWishlistEntity =
    SharedWishlistEntity(
      id = newUuid(),
      wishlist = request.wishlistId,
      owner = request.owner,
      editors = request.editors,
      group = request.group,
      participants = emptyList(),
      editorsCanSeeUpdates = request.editorsCanSeeUpdates,
      inviteLink = request.shareLink.token,
      deadline = request.deadline,
      sharedAt = System.currentTimeMillis(),
    )
}