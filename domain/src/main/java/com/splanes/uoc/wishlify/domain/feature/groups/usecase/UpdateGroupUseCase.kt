package com.splanes.uoc.wishlify.domain.feature.groups.usecase

import androidx.core.net.toUri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

/**
 * Updates an existing group for the current user.
 *
 * It also applies group lifecycle rules, including deleting the group when the
 * current user leaves and only two members would remain in the request.
 */
class UpdateGroupUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val imageMediaRepository: ImageMediaRepository,
  private val repository: GroupsRepository
) : UseCase() {

  /**
   * Updates the group described by [request].
   */
  suspend operator fun invoke(
    request: UpdateGroupRequest
  ) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->

        // Leaving group && group members = 2
        if (request.members.count() == 2 && !request.includeCurrentUser) {
          repository.deleteGroup(request.id).getOrThrow()
        } else {
          val imageMedia = imageMediaOf(
            groupId = request.id,
            request = request.image
          )

          repository.updateGroup(uid, imageMedia, request).getOrThrow()
        }
      }
  }

  /**
   * Resolves the media reference that should be stored for the updated group image.
   *
   * When the image is removed, the existing stored asset is deleted.
   */
  private suspend fun imageMediaOf(
    groupId: String,
    request: ImageMediaRequest?
  ): ImageMedia? =
    when (request) {
      is ImageMediaRequest.Device -> {
        val url = imageMediaRepository.upload(
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

      null -> {
        imageMediaRepository.delete(ImageMediaPath.Group(groupId = groupId))
        null
      }
    }
}
