package com.splanes.uoc.wishlify.domain.feature.groups.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.feature.groups.model.UpdateGroupRequest
import com.splanes.uoc.wishlify.domain.feature.groups.repository.GroupsRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UpdateGroupUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val imageMediaRepository: ImageMediaRepository = mock()
  private val repository: GroupsRepository = mock()

  private lateinit var useCase: UpdateGroupUseCase

  @Before
  fun setup() {
    useCase = UpdateGroupUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      imageMediaRepository = imageMediaRepository,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val request = updateGroupRequest()
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository, never()).deleteGroup(any())
    verify(repository, never()).updateGroup(any(), anyOrNull(), any())
  }

  @Test
  fun `delete group when leaving group and remaining members are two`() = runTest {
    val request = updateGroupRequest(
      members = listOf("u1", "u2"),
      includeCurrentUser = false
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success("uid"))
    whenever(repository.deleteGroup(request.id)).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).deleteGroup(request.id)
    verify(repository, never()).updateGroup(any(), anyOrNull(), any())
  }

  @Test
  fun `return failure when delete group fails`() = runTest {
    val request = updateGroupRequest(
      members = listOf("u1", "u2"),
      includeCurrentUser = false
    )
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success("uid"))
    whenever(repository.deleteGroup(request.id)).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).deleteGroup(request.id)
  }

  @Test
  fun `delete image and update group with null image when request image is null`() = runTest {
    val uid = "uid"
    val request = updateGroupRequest(image = null)

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(imageMediaRepository.delete(ImageMediaPath.Group(request.id)))
      .thenReturn(Result.success(Unit))
    whenever(repository.updateGroup(uid, null, request))
      .thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(imageMediaRepository).delete(ImageMediaPath.Group(request.id))
    verify(repository).updateGroup(uid, null, request)
  }

  @Test
  fun `update group with url image media when request image is url`() = runTest {
    val uid = "uid"
    val request = updateGroupRequest(
      image = ImageMediaRequest.Url("https://image.test/photo.jpg")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.updateGroup(
        eq(uid),
        eq(ImageMedia.Url("https://image.test/photo.jpg")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).updateGroup(
      uid,
      ImageMedia.Url("https://image.test/photo.jpg"),
      request
    )
  }

  @Test
  fun `update group with preset image media when request image is preset`() = runTest {
    val uid = "uid"
    val request = updateGroupRequest(
      image = ImageMediaRequest.Preset("preset-id")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.updateGroup(
        eq(uid),
        eq(ImageMedia.Preset("preset-id")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).updateGroup(
      uid,
      ImageMedia.Preset("preset-id"),
      request
    )
  }

  @Test
  fun `return failure when update group fails`() = runTest {
    val uid = "uid"
    val request = updateGroupRequest(image = null)
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(imageMediaRepository.delete(ImageMediaPath.Group(request.id)))
      .thenReturn(Result.success(Unit))
    whenever(repository.updateGroup(uid, null, request))
      .thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).updateGroup(uid, null, request)
  }

  private fun updateGroupRequest(
    id: String = "group-id",
    name: String = "Group name",
    members: List<String> = listOf("u1", "u2", "u3"),
    image: ImageMediaRequest? = null,
    includeCurrentUser: Boolean = true,
  ) = UpdateGroupRequest(
    id = id,
    name = name,
    members = members,
    image = image,
    includeCurrentUser = includeCurrentUser,
  )
}