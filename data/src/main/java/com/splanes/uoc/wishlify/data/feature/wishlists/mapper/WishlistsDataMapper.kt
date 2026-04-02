package com.splanes.uoc.wishlify.data.feature.wishlists.mapper

import com.splanes.uoc.wishlify.data.common.media.mapper.ImageMediaDataMapper
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.user.model.UserBasic
import com.splanes.uoc.wishlify.data.feature.wishlists.model.CategoryEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistEntity
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import java.util.Date

class WishlistsDataMapper(
  private val imageMediaMapper: ImageMediaDataMapper,
  private val userDataMapper: UserDataMapper,
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

  fun mapWishlist(
    uid: String,
    entity: WishlistEntity,
    category: CategoryEntity?,
    users: List<UserBasic>,
  ): Wishlist =
    when (entity.type) {
      WishlistEntity.Type.Own ->
        Wishlist.Own(
          id = entity.id,
          title = entity.title,
          description = entity.description,
          photo = imageMediaMapper.map(entity.photo),
          category = category?.let {
            Wishlist.WishlistCategory(
              category = mapCategory(entity = category),
              owner = entity.category?.owner.orEmpty(),
              isOwn = entity.category?.owner.orEmpty() == uid
            )
          },
          editorInviteLink = InviteLink(
            token = entity.editorInviteLink,
            origin = InviteLink.WishlistsEditor
          ),
          editors = users
            .filter { u -> u.uid in entity.editors }
            .map(userDataMapper::map),
          createdBy = users
            .first { u -> u.uid == entity.createdBy }
            .let(userDataMapper::map),
          createdAt = Date(entity.createdAt),
          lastUpdate = Wishlist.UpdateMetadata(
            updatedBy = users
              .first { u -> u.uid == entity.lastUpdate.updatedBy }
              .let(userDataMapper::map),
            updatedAt = Date(entity.lastUpdate.updatedAt)
          ),
        )

      WishlistEntity.Type.ThirdParty ->
        Wishlist.ThirdParty(
          id = entity.id,
          title = entity.title,
          description = entity.description,
          photo = imageMediaMapper.map(entity.photo),
          category = category?.let {
            Wishlist.WishlistCategory(
              category = mapCategory(entity = category),
              owner = entity.category?.owner.orEmpty(),
              isOwn = entity.category?.owner.orEmpty() == uid
            )
          },
          editorInviteLink = InviteLink(
            token = entity.editorInviteLink,
            origin = InviteLink.WishlistsEditor
          ),
          editors = users
            .filter { u -> u.uid in entity.editors }
            .map(userDataMapper::map),
          createdBy = users
            .first { u -> u.uid == entity.createdBy }
            .let(userDataMapper::map),
          target = entity.target ?: error("No 'target' specified but 'type' is ThirdParty"),
          createdAt = Date(entity.createdAt),
          lastUpdate = Wishlist.UpdateMetadata(
            updatedBy = users
              .first { u -> u.uid == entity.lastUpdate.updatedBy }
              .let(userDataMapper::map),
            updatedAt = Date(entity.lastUpdate.updatedAt)
          ),
        )
    }

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
          owner = uid,
          id = category.id
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