package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.secretsanta.helper.SecretSantaDrawExecutor
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ValidateSecretSantaDrawUseCaseShould {

  private val executor: SecretSantaDrawExecutor = mock()

  private lateinit var useCase: ValidateSecretSantaDrawUseCase

  @Before
  fun setup() {
    useCase = ValidateSecretSantaDrawUseCase(executor)
  }

  @Test
  fun `return true when executor says draw is feasible`() {
    val participants = listOf(
      user("a"),
      user("b"),
      user("c")
    )
    val exclusions = emptyList<Pair<User.Basic, User.Basic>>()

    whenever(
      executor.isFeasible(
        participants = listOf("a", "b", "c"),
        exclusions = emptyMap()
      )
    ).thenReturn(true)

    val result = useCase(participants, exclusions)

    assertThat(result).isTrue()
    verify(executor).isFeasible(
      participants = listOf("a", "b", "c"),
      exclusions = emptyMap()
    )
  }

  @Test
  fun `return false when executor says draw is not feasible`() {
    val participants = listOf(
      user("a"),
      user("b"),
      user("c")
    )
    val exclusions = emptyList<Pair<User.Basic, User.Basic>>()

    whenever(
      executor.isFeasible(
        participants = listOf("a", "b", "c"),
        exclusions = emptyMap()
      )
    ).thenReturn(false)

    val result = useCase(participants, exclusions)

    assertThat(result).isFalse()
    verify(executor).isFeasible(
      participants = listOf("a", "b", "c"),
      exclusions = emptyMap()
    )
  }

  @Test
  fun `map exclusions grouped by giver uid`() {
    val a = user("a")
    val b = user("b")
    val c = user("c")
    val d = user("d")

    val participants = listOf(a, b, c, d)
    val exclusions = listOf(
      a to b,
      a to c,
      b to d
    )

    whenever(
      executor.isFeasible(
        eq(listOf("a", "b", "c", "d")),
        eq(
          mapOf(
            "a" to listOf("b", "c"),
            "b" to listOf("d")
          )
        )
      )
    ).thenReturn(true)

    val result = useCase(participants, exclusions)

    assertThat(result).isTrue()
    verify(executor).isFeasible(
      listOf("a", "b", "c", "d"),
      mapOf(
        "a" to listOf("b", "c"),
        "b" to listOf("d")
      )
    )
  }

  private fun user(uid: String) = User.Basic(
    uid = uid,
    username = "user_$uid",
    photoUrl = null,
    code = ""
  )
}