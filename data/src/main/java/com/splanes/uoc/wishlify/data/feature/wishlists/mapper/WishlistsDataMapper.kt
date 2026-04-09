package com.splanes.uoc.wishlify.data.feature.wishlists.mapper

import com.splanes.uoc.wishlify.data.common.media.mapper.ImageMediaDataMapper
import com.splanes.uoc.wishlify.data.common.utils.nowInMillis
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.user.model.UserBasic
import com.splanes.uoc.wishlify.data.feature.wishlists.model.CategoryEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistItemEntity
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistItemRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistItemRequest
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
    numOfItemsMap: Map<String, Int>,
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
          numOfItems = numOfItemsMap[entity.id] ?: 0,
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
          numOfItems = numOfItemsMap[entity.id] ?: 0,
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
      createdAt = nowInMillis(),
      lastUpdate = WishlistEntity.UpdateMetadata(
        updatedBy = uid,
        updatedAt = nowInMillis()
      ),
    )

  fun shareWishlist(
    uid: String,
    sharedWishlistId: String,
    entity: WishlistEntity,
  ): WishlistEntity =
    entity.copy(
      shareStatus = WishlistEntity.ShareStatus.Shared,
      sharedWishlistId = sharedWishlistId,
      lastUpdate = WishlistEntity.UpdateMetadata(
        updatedAt = nowInMillis(),
        updatedBy = uid
      )
    )

  fun mapItem(
    entity: WishlistItemEntity,
    users: List<UserBasic>,
  ): WishlistItem =
    WishlistItem(
      id = entity.id,
      photoUrl = entity.photoUrl,
      name = entity.name,
      description = entity.description.orEmpty(),
      store = entity.store.orEmpty(),
      unitPrice = entity.unitPrice,
      amount = entity.amount,
      priority = when (entity.priority) {
        WishlistItemEntity.Priority.Standard -> WishlistItem.Priority.Standard
        WishlistItemEntity.Priority.Top -> WishlistItem.Priority.Top
        WishlistItemEntity.Priority.Supertop -> WishlistItem.Priority.Supertop
      },
      link = entity.link.orEmpty(),
      tags = entity.tags.filter { it.isNotBlank() },
      createdBy = users
        .first { u -> u.uid == entity.createdBy }
        .let(userDataMapper::map),
      createdAt = Date(entity.createdAt),
      lastUpdate = WishlistItem.UpdateMetadata(
        updatedBy = users
          .first { u -> u.uid == entity.lastUpdate.updatedBy }
          .let(userDataMapper::map),
        updatedAt = Date(entity.lastUpdate.updatedAt)
      ),
      purchased = entity.purchased?.let { purchased ->
        WishlistItem.PurchaseMetadata(
          purchasedBy = users
            .first { u -> u.uid == purchased.purchasedBy }
            .let(userDataMapper::map),
          purchasedAt = Date(purchased.purchasedAt)
        )
      }
    )

  fun wishlistItemFromRequest(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateWishlistItemRequest
  ): WishlistItemEntity =
    WishlistItemEntity(
      id = request.id,
      photoUrl = imageMedia?.let { media ->
        when (media) {
          is ImageMedia.Preset -> null
          is ImageMedia.Url -> media.url
        }
      },
      name = request.name,
      description = request.description,
      store = request.store,
      unitPrice = request.price,
      amount = request.amount,
      priority = when (request.priority) {
        WishlistItem.Priority.Standard -> WishlistItemEntity.Priority.Standard
        WishlistItem.Priority.Top -> WishlistItemEntity.Priority.Top
        WishlistItem.Priority.Supertop -> WishlistItemEntity.Priority.Supertop
      },
      link = request.link,
      tags = request.tags,
      createdBy = uid,
      createdAt = nowInMillis(),
      lastUpdate = WishlistItemEntity.UpdateMetadata(
        updatedBy = uid,
        updatedAt = nowInMillis()
      ),
      purchased = null,
    )

  fun wishlistItemFromRequest(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateWishlistItemRequest
  ): WishlistItemEntity =
    WishlistItemEntity(
      id = request.currentItem.id,
      photoUrl = imageMedia?.let { media ->
        when (media) {
          is ImageMedia.Preset -> null
          is ImageMedia.Url -> media.url
        }
      },
      name = request.name,
      description = request.description,
      store = request.store,
      unitPrice = request.price,
      amount = request.amount,
      priority = when (request.priority) {
        WishlistItem.Priority.Standard -> WishlistItemEntity.Priority.Standard
        WishlistItem.Priority.Top -> WishlistItemEntity.Priority.Top
        WishlistItem.Priority.Supertop -> WishlistItemEntity.Priority.Supertop
      },
      link = request.link,
      tags = request.tags,
      createdBy = uid,
      createdAt = nowInMillis(),
      lastUpdate = WishlistItemEntity.UpdateMetadata(
        updatedBy = uid,
        updatedAt = nowInMillis()
      ),
      purchased = when (request.purchased) {
        UpdateWishlistItemRequest.Purchased ->
          WishlistItemEntity.PurchaseMetadata(
            purchasedBy = uid,
            purchasedAt = nowInMillis()
          )

        UpdateWishlistItemRequest.Available -> null

        else -> request.currentItem.purchased?.let { purchase ->
          WishlistItemEntity.PurchaseMetadata(
            purchasedBy = purchase.purchasedBy.uid,
            purchasedAt = purchase.purchasedAt.time
          )
        }
      },
    )

  fun mapToLinkedWishlist(entity: WishlistEntity): SharedWishlist.LinkedWishlist =
    SharedWishlist.LinkedWishlist(
      id = entity.id,
      name = entity.title,
      photo = imageMediaMapper.map(entity.photo),
      target = entity.target
    )

  fun mapToLinkedItem(entity: WishlistItemEntity): SharedWishlistItem.LinkedItem =
    SharedWishlistItem.LinkedItem(
      id = entity.id,
      photoUrl = entity.photoUrl,
      name = entity.name,
      description = entity.description.orEmpty(),
      store = entity.store.orEmpty(),
      unitPrice = entity.unitPrice,
      amount = entity.amount,
      priority = when (entity.priority) {
        WishlistItemEntity.Priority.Standard -> WishlistItem.Priority.Standard
        WishlistItemEntity.Priority.Top -> WishlistItem.Priority.Top
        WishlistItemEntity.Priority.Supertop -> WishlistItem.Priority.Supertop
      },
      link = entity.link.orEmpty(),
    )
}