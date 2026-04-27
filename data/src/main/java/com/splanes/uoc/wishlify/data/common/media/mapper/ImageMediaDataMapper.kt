package com.splanes.uoc.wishlify.data.common.media.mapper

import com.splanes.uoc.wishlify.data.common.media.model.ImageMediaEntity
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath

/**
 * Maps image media representations between data entities, domain models and storage paths.
 */
class ImageMediaDataMapper {

  /** Maps a domain image media model into its persisted data representation. */
  fun map(media: ImageMedia): ImageMediaEntity =
    when (media) {
      is ImageMedia.Preset ->
        ImageMediaEntity(
          type = ImageMediaEntity.Type.Preset,
          value = media.id
        )

      is ImageMedia.Url ->
        ImageMediaEntity(
          type = ImageMediaEntity.Type.Url,
          value = media.url
        )
    }

  /** Maps a persisted image media entity into the corresponding domain model. */
  fun map(entity: ImageMediaEntity): ImageMedia =
    when (entity.type) {
      ImageMediaEntity.Type.Url -> ImageMedia.Url(url = entity.value)
      ImageMediaEntity.Type.Preset -> ImageMedia.Preset(id = entity.value)
    }

  /** Resolves the Firebase Storage path associated with a domain media location. */
  fun pathOf(path: ImageMediaPath): String =
    when (path) {
      is ImageMediaPath.WishlistCover ->
        WISHLIST_COVER.format(path.wishlistId)

      is ImageMediaPath.WishlistItem ->
        WISHLIST_ITEM_PHOTO.format(path.wishlistId, path.itemId)

      is ImageMediaPath.Group ->
        GROUP_PHOTO.format(path.groupId)

      is ImageMediaPath.SecretSanta ->
        SECRET_SANTA_COVER.format(path.secretSantaId)

      is ImageMediaPath.Profile ->
        PROFILE_PHOTO.format(path.uid)
    }
}

private const val WISHLIST_COVER = "wishlists/%s/cover"
private const val WISHLIST_ITEM_PHOTO = "wishlists/%s/items/%s"
private const val SECRET_SANTA_COVER = "secret-santa/%s/cover"
private const val GROUP_PHOTO = "groups/%s"
private const val PROFILE_PHOTO = "profile/%s"
