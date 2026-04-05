package com.splanes.uoc.wishlify.data.common.media.mapper

import com.splanes.uoc.wishlify.data.common.media.model.ImageMediaEntity
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath

class ImageMediaDataMapper {

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

  fun map(entity: ImageMediaEntity): ImageMedia =
    when (entity.type) {
      ImageMediaEntity.Type.Url -> ImageMedia.Url(url = entity.value)
      ImageMediaEntity.Type.Preset -> ImageMedia.Preset(id = entity.value)
    }

  fun pathOf(path: ImageMediaPath): String =
    when (path) {
      is ImageMediaPath.WishlistCover -> {
        WISHLIST_COVER.format(path.wishlistId)
      }

      is ImageMediaPath.WishlistItem ->
        WISHLIST_ITEM_PHOTO.format(path.wishlistId, path.itemId)
    }
}

private const val WISHLIST_COVER = "wishlists/%s/cover"
private const val WISHLIST_ITEM_PHOTO = "wishlists/%s/items/%s"