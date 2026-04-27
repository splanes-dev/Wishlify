package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.CreateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/**
 * Creates a new Secret Santa event for the current user.
 *
 * When the request includes a device image, it uploads it first and passes the
 * resulting media reference to the repository.
 */
class CreateSecretSantaEventUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val imageMediaRepository: ImageMediaRepository,
  private val repository: SecretSantaRepository
) : UseCase() {

  /** Creates the event described by [request]. */
  suspend operator fun invoke(request: CreateSecretSantaEventRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        val imageMedia = request.image?.let { image ->
          imageMediaOf(
            secretSantaId = request.id,
            request = image
          )
        }

        repository.createSecretSantaEvent(uid, imageMedia, request).getOrThrow()
      }
  }

  /** Resolves the final media reference that should be stored for the event image. */
  private suspend fun imageMediaOf(
    secretSantaId: String,
    request: ImageMediaRequest
  ): ImageMedia =
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
    }
}
