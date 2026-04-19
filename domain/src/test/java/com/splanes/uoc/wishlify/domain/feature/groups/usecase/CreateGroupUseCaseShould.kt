package com.splanes.uoc.wishlify.domain.feature.groups.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.feature.groups.model.CreateGroupRequest
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

class CreateGroupUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val repository: GroupsRepository = mock()
  private val mediaRepository: ImageMediaRepository = mock()

  private lateinit var useCase: CreateGroupUseCase

  @Before
  fun setup() {
    useCase = CreateGroupUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      repository = repository,
      mediaRepository = mediaRepository,
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val request = createGroupRequest()
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository, never()).addGroup(any(), anyOrNull(), any())
  }

  @Test
  fun `create group with no image when request image is null`() = runTest {
    val uid = "uid"
    val request = createGroupRequest(image = null)

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.addGroup(uid, null, request)).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).addGroup(uid, null, request)
  }

  @Test
  fun `create group with url image media when request image is url`() = runTest {
    val uid = "uid"
    val request = createGroupRequest(
      image = ImageMediaRequest.Url("https://image.test/photo.jpg")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.addGroup(
        eq(uid),
        eq(ImageMedia.Url("https://image.test/photo.jpg")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).addGroup(
      uid,
      ImageMedia.Url("https://image.test/photo.jpg"),
      request
    )
  }

  @Test
  fun `create group with preset image media when request image is preset`() = runTest {
    val uid = "uid"
    val request = createGroupRequest(
      image = ImageMediaRequest.Preset("preset-id")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.addGroup(
        eq(uid),
        eq(ImageMedia.Preset("preset-id")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).addGroup(
      uid,
      ImageMedia.Preset("preset-id"),
      request
    )
  }

  @Test
  fun `return failure when add group fails`() = runTest {
    val uid = "uid"
    val request = createGroupRequest(image = null)
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.addGroup(uid, null, request)).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).addGroup(uid, null, request)
  }

  private fun createGroupRequest(
    id: String = "group-id",
    name: String = "My group",
    image: ImageMediaRequest? = null,
  ) = CreateGroupRequest(
    id = id,
    name = name,
    image = image,
    members = emptyList()
  )
}