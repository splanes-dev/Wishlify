package com.splanes.uoc.wishlify.domain.feature.groups.usecase

import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/**
 * Creates a new group for the current user.
 *
 * When the request includes a device image, it uploads it first and passes the
 * resulting media reference to the repository.
 */
class CreateGroupUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val repository: GroupsRepository,
  private val mediaRepository: ImageMediaRepository,
) : UseCase() {

  /**
   * Creates the group described by [request].
   */
  suspend operator fun invoke(request: CreateGroupRequest) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->
        val imageMedia = request.image?.let { image ->
          imageMediaOf(
            groupId = request.id,
            request = image
          )
        }

        repository.addGroup(uid, imageMedia, request).getOrThrow()
      }
  }

  /**
   * Resolves the final media reference that should be stored for the group image.
   */
  private suspend fun imageMediaOf(
    groupId: String,
    request: ImageMediaRequest
  ): ImageMedia =
    when (request) {
      is ImageMediaRequest.Device -> {
        val url = mediaRepository.upload(
          path = ImageMediaPath.Group(groupId = groupId),
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
