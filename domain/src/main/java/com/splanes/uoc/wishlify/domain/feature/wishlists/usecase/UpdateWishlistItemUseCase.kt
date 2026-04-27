package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistItemRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/**
 * Updates an existing wishlist item for the current user.
 *
 * When the request includes a device image, it uploads it first and passes the
 * resulting media reference to the repository.
 */
class UpdateWishlistItemUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val mediaRepository: ImageMediaRepository,
  private val repository: WishlistsRepository
) : UseCase() {

  /** Updates the item described by [request]. */
  suspend operator fun invoke(request: UpdateWishlistItemRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->

        val imageMedia = request.photo?.let { photo ->
          imageMediaOf(
            wishlist = request.wishlist,
            item = request.currentItem.id,
            request = photo
          )
        }

        repository.updateWishlistItem(uid, imageMedia, request).getOrThrow()
      }
  }

  /** Resolves the final media reference that should be stored for the item photo. */
  private suspend fun imageMediaOf(
    wishlist: String,
    item: String,
    request: ImageMediaRequest): ImageMedia =
    when (request) {
      is ImageMediaRequest.Device -> {
        val url = mediaRepository.upload(
          path = ImageMediaPath.WishlistItem(wishlistId = wishlist, itemId = item),
          uri = request.uri.toUri()
        ).getOrThrow()

        ImageMedia.Url(url)
      }

      is ImageMediaRequest.Preset -> {
        ImageMedia.Preset(request.id)
      }

      is ImageMediaRequest.Url -> {
        ImageMedia.Url(request.url)
      }
    }
}
