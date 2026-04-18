package com.splanes.uoc.wishlify.domain.feature.user.usecase

import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import com.splanes.uoc.wishlify.domain.feature.authentication.usecase.SignOutUseCase
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository

class UpdateUserProfileUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val signOutUseCase: SignOutUseCase,
  private val imageMediaRepository: ImageMediaRepository,
  private val authenticationRepository: AuthenticationRepository,
  private val repository: UserRepository
) : UseCase() {

  suspend operator fun invoke(request: UpdateProfileRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->

        val imageMedia = request.mediaOrNull()?.let { imageMediaOf(uid, it) }

        repository.updateProfile(request, imageMedia).getOrThrow()

        if (request is UpdateProfileRequest.BasicInfo && request.hasChangedEmail()) {
          val localCredentials = authenticationRepository.fetchStoredCredentials()
          authenticationRepository.updateEmail(localCredentials, request.email).getOrThrow()
          signOutUseCase().getOrThrow()
        }
      }
  }

  private fun UpdateProfileRequest.BasicInfo.hasChangedEmail() = user.email != email

  private suspend fun imageMediaOf(
    uid: String,
    request: ImageMediaRequest
  ): ImageMedia =
    when (request) {
      is ImageMediaRequest.Device -> {
        val url = imageMediaRepository.upload(
          path = ImageMediaPath.Profile(uid = uid),
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