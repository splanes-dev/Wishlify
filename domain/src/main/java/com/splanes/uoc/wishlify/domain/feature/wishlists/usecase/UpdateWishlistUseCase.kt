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

class UpdateWishlistUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: WishlistsRepository,
  private val mediaRepository: ImageMediaRepository,
) : UseCase() {

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