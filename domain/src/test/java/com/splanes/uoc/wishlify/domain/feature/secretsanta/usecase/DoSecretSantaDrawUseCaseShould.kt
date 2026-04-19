package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.secretsanta.helper.SecretSantaDrawExecutor
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class DoSecretSantaDrawUseCaseShould {

  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mock()
  private val executor: SecretSantaDrawExecutor = mock()
  private val repository: SecretSantaRepository = mock()

  private lateinit var useCase: DoSecretSantaDrawUseCase

  @Before
  fun setup() {
    useCase = DoSecretSantaDrawUseCase(
      getCurrentUserIdUseCase = getCurrentUserIdUseCase,
      executor = executor,
      repository = repository
    )
  }

  @Test
  fun `return failure when current user id fetch fails`() = runTest {
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.failure(error))

    val result = useCase(secretSantaEvent())

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(executor, never()).executeOrThrow(any(), any())
  }

  @Test
  fun `build participants correctly and execute draw`() = runTest {
    val uid = "current"
    val event = secretSantaEvent(
      createdBy = user("a"),
      participants = listOf(user("b"), user("c")),
      group = Group.Basic(id = "", name = "", photoUrl = null, state = Group.State.Active, listOf("c", "d")) // c duplicado
    )

    val expectedParticipants = listOf("a", "b", "c", "d")
    val assignments = mapOf("a" to "b", "b" to "c", "c" to "d", "d" to "a")

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(executor.executeOrThrow(any(), any())).thenReturn(assignments)
    whenever(repository.doSecretSantaDraw(uid, event.id, assignments))
      .thenReturn(Result.success(Unit))

    val result = useCase(event)

    assertThat(result.isSuccess).isTrue()

    verify(executor).executeOrThrow(
      eq(expectedParticipants),
      any()
    )
  }

  @Test
  fun `build exclusions correctly`() = runTest {
    val uid = "current"
    val event = secretSantaEvent(
      exclusions = mapOf(
        user("a") to listOf(user("b"), user("c"))
      )
    )

    val assignments = emptyMap<String, String>()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(executor.executeOrThrow(any(), any())).thenReturn(assignments)
    whenever(repository.doSecretSantaDraw(any(), any(), any()))
      .thenReturn(Result.success(Unit))

    useCase(event)

    verify(executor).executeOrThrow(
      any(),
      eq(mapOf("a" to listOf("b", "c")))
    )
  }

  @Test
  fun `return failure when executor throws`() = runTest {
    val uid = "current"
    val event = secretSantaEvent()
    val error = IllegalStateException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(executor.executeOrThrow(any(), any())).thenThrow(error)

    val result = useCase(event)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
    verify(repository, never()).doSecretSantaDraw(any(), any(), any())
  }

  @Test
  fun `return failure when repository fails`() = runTest {
    val uid = "current"
    val event = secretSantaEvent()
    val assignments = mapOf("a" to "b")
    val error = RuntimeException()

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(executor.executeOrThrow(any(), any())).thenReturn(assignments)
    whenever(repository.doSecretSantaDraw(uid, event.id, assignments))
      .thenReturn(Result.failure(error))

    val result = useCase(event)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
  }

  @Test
  fun `execute draw successfully`() = runTest {
    val uid = "current"
    val event = secretSantaEvent()
    val assignments = mapOf("a" to "b")

    whenever(getCurrentUserIdUseCase()).thenReturn(Result.success(uid))
    whenever(executor.executeOrThrow(any(), any())).thenReturn(assignments)
    whenever(repository.doSecretSantaDraw(uid, event.id, assignments))
      .thenReturn(Result.success(Unit))

    val result = useCase(event)

    assertThat(result.isSuccess).isTrue()
    verify(repository).doSecretSantaDraw(uid, event.id, assignments)
  }


  private fun secretSantaEvent(
    id: String = "",
    photoUrl: String? = null,
    name: String = "",
    budget: Double = 1.2,
    isBudgetApproximate: Boolean = false,
    group: Group.Basic? = null,
    participants: List<User.Basic> = emptyList(),
    exclusions: Map<User.Basic, List<User.Basic>> = emptyMap(),
    deadline: Date = Date(1L),
    inviteLink: InviteLink = InviteLink(token = "", origin = InviteLink.SecretSanta),
    createdBy: User.Basic = user(),
    createdAt: Date = Date(1L),
    receiver: User.Basic = user(),
    receiverSharedWishlist: String? = null,
    receiverSharedHobbies: Boolean = false,
    currentUserSharedWishlist: String? = null,
    receiverChatNotificationCount: Int = 0,
    giver: String = "",
    giverChatNotificationCount: Int = 0,
  ): SecretSantaEventDetail {
    return SecretSantaEventDetail.DrawDone(
      id = id,
      photoUrl = photoUrl,
      name = name,
      budget = budget,
      isBudgetApproximate = isBudgetApproximate,
      group = group,
      participants = participants,
      exclusions = exclusions,
      deadline = deadline,
      inviteLink = inviteLink,
      createdBy = createdBy,
      createdAt = createdAt,
      receiver = receiver,
      receiverSharedWishlist = receiverSharedWishlist,
      receiverSharedHobbies = receiverSharedHobbies,
      currentUserSharedWishlist = currentUserSharedWishlist,
      receiverChatNotificationCount = receiverChatNotificationCount,
      giver = giver,
      giverChatNotificationCount = giverChatNotificationCount,
    )
  }

  private fun user(uid: String = "") =
    User.Basic(
      uid = uid,
      username = "",
      code = "",
      photoUrl = null
    )
}