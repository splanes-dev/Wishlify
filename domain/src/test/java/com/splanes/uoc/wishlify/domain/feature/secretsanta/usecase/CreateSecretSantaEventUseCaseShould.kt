package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.CreateSecretSantaEventRequest
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

class CreateSecretSantaEventUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val imageMediaRepository: ImageMediaRepository = mock()
  private val repository: SecretSantaRepository = mock()

  private lateinit var useCase: CreateSecretSantaEventUseCase

  @Before
  fun setup() {
    useCase = CreateSecretSantaEventUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      imageMediaRepository = imageMediaRepository,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val request = createRequest()
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository, never()).createSecretSantaEvent(any(), anyOrNull(), any())
  }

  @Test
  fun `create event with no image when request image is null`() = runTest {
    val uid = "uid"
    val request = createRequest(image = null)

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.createSecretSantaEvent(uid, null, request))
      .thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).createSecretSantaEvent(uid, null, request)
  }

  @Test
  fun `create event with url image media when request image is url`() = runTest {
    val uid = "uid"
    val request = createRequest(
      image = ImageMediaRequest.Url("https://image.test/photo.jpg")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.createSecretSantaEvent(
        eq(uid),
        eq(ImageMedia.Url("https://image.test/photo.jpg")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).createSecretSantaEvent(
      uid,
      ImageMedia.Url("https://image.test/photo.jpg"),
      request
    )
  }

  @Test
  fun `create event with preset image media when request image is preset`() = runTest {
    val uid = "uid"
    val request = createRequest(
      image = ImageMediaRequest.Preset("preset-id")
    )

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(
      repository.createSecretSantaEvent(
        eq(uid),
        eq(ImageMedia.Preset("preset-id")),
        eq(request)
      )
    ).thenReturn(Result.success(Unit))

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(repository).createSecretSantaEvent(
      uid,
      ImageMedia.Preset("preset-id"),
      request
    )
  }

  @Test
  fun `return failure when create event fails`() = runTest {
    val uid = "uid"
    val request = createRequest(image = null)
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(repository.createSecretSantaEvent(uid, null, request))
      .thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository).createSecretSantaEvent(uid, null, request)
  }

  private fun createRequest(
    id: String = "event-id",
    name: String = "Secret Santa",
    image: ImageMediaRequest? = null,
    budget: Double = 10.0,
    isBudgetApproximate: Boolean = false,
    deadline: Date = Date(1L),
    group: Group.Basic? = null,
    participants: List<User.Basic> = emptyList(),
    exclusions: List<Pair<User.Basic, User.Basic>> = emptyList(),
    inviteLink: InviteLink = InviteLink(token = "", origin = InviteLink.Origin.SecretSanta),
  ) = CreateSecretSantaEventRequest(
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