package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.UpdateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/**
 * Updates an existing Secret Santa event for the current user.
 *
 * When the request contains a device image, it uploads it first. When the
 * image is removed, the stored asset is deleted.
 */
class UpdateSecretSantaEventUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val imageMediaRepository: ImageMediaRepository,
  private val repository: SecretSantaRepository
) : UseCase() {

  /** Updates the event described by [request]. */
  suspend operator fun invoke(request: UpdateSecretSantaEventRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        val imageMedia = imageMediaOf(
          secretSantaId = request.id,
          request = request.image
        )

        repository.updateSecretSantaEvent(uid, imageMedia, request).getOrThrow()
      }
  }

  /** Resolves the media reference that should be stored for the updated event image. */
  private suspend fun imageMediaOf(
    secretSantaId: String,
    request: ImageMediaRequest?
  ): ImageMedia? =
    when (request) {
      is ImageMediaRequest.Device -> {
        val url = imageMediaRepository.upload(
          path = ImageMediaPath.SecretSanta(secretSantaId = secretSantaId),
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

      null -> {
        imageMediaRepository.delete(ImageMediaPath.SecretSanta(secretSantaId = secretSantaId))
        null
      }
    }
}
