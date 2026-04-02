package com.splanes.uoc.wishlify.data.feature.wishlists.mapper

import com.splanes.uoc.wishlify.data.common.media.mapper.ImageMediaDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.model.CategoryEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistEntity
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.CreateWishlistRequest

class WishlistsDataMapper(
  private val imageMediaMapper: ImageMediaDataMapper,
) {

  fun mapCategory(entity: CategoryEntity): Category =
    Category(
      id = entity.id,
      name = entity.name,
      color = Category.CategoryColor.from(entity.color)
    )

  fun mapCategory(category: Category): CategoryEntity =
    CategoryEntity(
      id = category.id,
      name = category.name,
      color = category.color.name.lowercase()
    )

  fun wishlistFromRequest(
    uid: String,
    imageMedia: ImageMedia,
    request: CreateWishlistRequest
  ): WishlistEntity =
    WishlistEntity(
      id = request.id,
      title = request.title,
      description = request.description,
      photo = imageMediaMapper.map(imageMedia),
      type = when (request) {
        is CreateWishlistRequest.Own -> WishlistEntity.Type.Own
        is CreateWishlistRequest.ThirdParty -> WishlistEntity.Type.ThirdParty
      },
      target = (request as? CreateWishlistRequest.ThirdParty)?.target,
      category = request.category?.let { category ->
        WishlistEntity.Category(
          uid = uid,
          categoryId = category.id
        )
      },
      editors = listOf(uid),
      editorInviteLink = request.editorInviteLink.token,
      createdBy = uid,
      createdAt = System.currentTimeMillis(),
      lastUpdate = WishlistEntity.UpdateMetadata(
        updatedBy = uid,
        updatedAt = System.currentTimeMillis()
      ),
    )
}