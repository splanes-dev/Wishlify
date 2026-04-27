package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/**
 * Creates a new wishlist for the current user.
 *
 * When the request includes a device image, it uploads it first and passes the
 * resulting media reference to the repository.
 */
class CreateWishlistUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository,
  private val mediaRepository: ImageMediaRepository,
) : UseCase() {

  /** Creates the wishlist described by [request]. */
  suspend operator fun invoke(request: CreateWishlistRequest): Result<Unit> = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        val imageMedia = imageMediaOf(id = request.id, request = request.media)

        repository.addWishlist(
          uid = uid,
          imageMedia = imageMedia,
          request = request
        ).getOrThrow()
      }
  }

  /** Resolves the final media reference that should be stored for the wishlist cover. */
  private suspend fun imageMediaOf(id: String, request: ImageMediaRequest): ImageMedia =
    when (request) {
      is ImageMediaRequest.Device -> {
        val url = mediaRepository.upload(
          path = ImageMediaPath.WishlistCover(id),
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
