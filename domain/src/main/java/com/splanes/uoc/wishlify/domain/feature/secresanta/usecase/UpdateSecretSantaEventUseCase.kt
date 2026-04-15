package com.splanes.uoc.wishlify.domain.feature.secresanta.usecase

import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secresanta.model.UpdateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secresanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

class UpdateSecretSantaEventUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val imageMediaRepository: ImageMediaRepository,
  private val repository: SecretSantaRepository
) : UseCase() {

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