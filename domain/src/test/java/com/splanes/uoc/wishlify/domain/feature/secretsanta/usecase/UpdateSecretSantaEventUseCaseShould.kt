package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.UpdateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
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
import java.util.Date

class UpdateSecretSantaEventUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val imageMediaRepository: ImageMediaRepository = mock()
  private val repository: SecretSantaRepository = mock()

  private lateinit var useCase: UpdateSecretSantaEventUseCase

  @Before
  fun setup() {
    useCase = UpdateSecretSantaEventUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      imageMediaRepository = imageMediaRepository,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val request = updateRequest()
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository, never()).updateSecretSantaEvent(any(), anyOrNull(), any())
  }

  @Test
  fun `delete image and update event with null image when request image is null`() = runTest {
    val uid = "uid"
    val request = updateRequest(image = null)

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(imageMediaRepository.delete(ImageMediaPath.SecretSanta(secretSantaId = request.id)))
      .thenReturn(Result.success(Unit))
    whenever(repository.updateSecretSantaEvent(uid, null, request))
      .thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(imageMediaRepository).delete(ImageMediaPath.SecretSanta(secretSantaId = request.id))
    verify(repository).updateSecretSantaEvent(uid, null, request)
  }

  @Test
  fun `update event with url image media when request image is url`() = runTest {
    val uid = "uid"
    val request = updateRequest(
      image = ImageMediaRequest.Url("https://image.test/photo.jpg")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.updateSecretSantaEvent(
        eq(uid),
        eq(ImageMedia.Url("https://image.test/photo.jpg")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).updateSecretSantaEvent(
      uid,
      ImageMedia.Url("https://image.test/photo.jpg"),
      request
    )
  }

  @Test
  fun `update event with preset image media when request image is preset`() = runTest {
    val uid = "uid"
    val request = updateRequest(
      image = ImageMediaRequest.Preset("preset-id")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.updateSecretSantaEvent(
        eq(uid),
        eq(ImageMedia.Preset("preset-id")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).updateSecretSantaEvent(
      uid,
      ImageMedia.Preset("preset-id"),
      request
    )
  }

  @Test
  fun `return failure when update event fails`() = runTest {
    val uid = "uid"
    val request = updateRequest(image = null)
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(imageMediaRepository.delete(ImageMediaPath.SecretSanta(secretSantaId = request.id)))
      .thenReturn(Result.success(Unit))
    whenever(repository.updateSecretSantaEvent(uid, null, request))
      .thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).updateSecretSantaEvent(uid, null, request)
  }

  private fun updateRequest(
    id: String = "event-id",
    name: String = "Secret Santa",
    image: ImageMediaRequest? = null,
    budget: Double = 20.0,
    isBudgetApproximate: Boolean = true,
    deadline: Date = Date(1L),
    group: Group.Basic? = null,
    participants: List<User.Basic> = emptyList(),
    exclusions: List<Pair<User.Basic, User.Basic>> = emptyList(),
    inviteLink: InviteLink = InviteLink(token = "", origin = InviteLink.SecretSanta),
  ) = UpdateSecretSantaEventRequest(
    id = id,
    name = name,
    image = image,
    budget = budget,
    isBudgetApproximate = isBudgetApproximate,
    deadline = deadline,
    group = group,
    participants = participants,
    exclusions = exclusions,
    inviteLink = inviteLink,
  )
}