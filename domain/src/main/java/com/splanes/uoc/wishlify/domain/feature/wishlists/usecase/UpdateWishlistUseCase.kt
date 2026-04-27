package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/**
 * Updates an existing wishlist for the current user.
 *
 * Device images are uploaded first. Switching to a preset or remote URL removes
 * the previously stored cover image.
 */
class UpdateWishlistUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository,
  private val mediaRepository: ImageMediaRepository,
) : UseCase() {

  /** Updates the wishlist described by [request]. */
  suspend operator fun invoke(request: UpdateWishlistRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->

        val imageMedia = imageMediaOf(
          id = request.currentWishlist.id,
          request = request.media
        )

        repository.updateWishlist(
          uid = uid,
          imageMedia = imageMedia,
          request = request
        ).getOrThrow()
      }
  }

  /** Resolves the final media reference that should be stored for the wishlist cover. */
  private suspend fun imageMediaOf(
    id: String,
    request: ImageMediaRequest
  ): ImageMedia =
    when (request) {
      is ImageMediaRequest.Device -> {
        val url = mediaRepository.upload(
          path = ImageMediaPath.WishlistCover(id),
          uri = request.uri.toUri()
        ).getOrThrow()

        ImageMedia.Url(url)
      }

      is ImageMediaRequest.Preset -> {
        mediaRepository.delete(ImageMediaPath.WishlistCover(id))
        ImageMedia.Preset(request.id)
      }

      is ImageMediaRequest.Url -> {
        mediaRepository.delete(ImageMediaPath.WishlistCover(id))
        ImageMedia.Url(request.url)
      }
    }
}
