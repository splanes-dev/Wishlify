package com.splanes.uoc.wishlify.domain.feature.secretsanta.helper


import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class SecretSantaDrawExecutorShould {

  private lateinit var executor: SecretSantaDrawExecutor

  @Before
  fun setup() {
    executor = SecretSantaDrawExecutor()
  }

  @Test
  fun `return false when there are less than two participants`() {
    val result = executor.isFeasible(
      participants = listOf("a"),
      exclusions = emptyMap()
    )

    assertThat(result).isFalse()
  }

  @Test
  fun `throw when executing draw with less than two participants`() {
    try {
      executor.executeOrThrow(
        participants = listOf("a"),
        exclusions = emptyMap()
      )
      throw AssertionError("Expected IllegalStateException")
    } catch (error: IllegalStateException) {
      assertThat(error).hasMessageThat().isEqualTo("Draw is not possible.")
    }
  }

  @Test
  fun `return true when draw is feasible without exclusions`() {
    val participants = listOf("a", "b", "c", "d")

    val result = executor.isFeasible(
      participants = participants,
      exclusions = emptyMap()
    )

    assertThat(result).isTrue()
  }

  @Test
  fun `execute valid draw without exclusions`() {
    val participants = listOf("a", "b", "c", "d")

    val result = executor.executeOrThrow(
      participants = participants,
      exclusions = emptyMap()
    )

    assertValidDraw(
      participants = participants,
      exclusions = emptyMap(),
      draw = result
    )
  }

  @Test
  fun `return true when draw is feasible with exclusions`() {
    val participants = listOf("a", "b", "c", "d")
    val exclusions = mapOf(
      "a" to listOf("b"),
      "b" to listOf("c")
    )

    val result = executor.isFeasible(
      participants = participants,
      exclusions = exclusions
    )

    assertThat(result).isTrue()
  }

  @Test
  fun `execute valid draw with exclusions`() {
    val participants = listOf("a", "b", "c", "d")
    val exclusions = mapOf(
      "a" to listOf("b"),
      "b" to listOf("c")
    )

    val result = executor.executeOrThrow(
      participants = participants,
      exclusions = exclusions
    )

    assertValidDraw(
      participants = participants,
      exclusions = exclusions,
      draw = result
    )
  }

  @Test
  fun `return false when one participant cannot gift to anybody`() {
    val participants = listOf("a", "b", "c")
    val exclusions = mapOf(
      "a" to listOf("b", "c")
    )

    val result = executor.isFeasible(
      participants = participants,
      exclusions = exclusions
    )

    assertThat(result).isFalse()
  }

  @Test
  fun `throw when one participant cannot gift to anybody`() {
    val participants = listOf("a", "b", "c")
    val exclusions = mapOf(
      "a" to listOf("b", "c")
    )

    try {
      executor.executeOrThrow(
        participants = participants,
        exclusions = exclusions
      )
      throw AssertionError("Expected IllegalStateException")
    } catch (error: IllegalStateException) {
      assertThat(error).hasMessageThat().isEqualTo("Draw is not possible.")
    }
  }

  @Test
  fun `return false when exclusions make perfect matching impossible`() {
    val participants = listOf("a", "b", "c")

    val exclusions = mapOf(
      "a" to listOf("b", "c"),
      "b" to listOf("a"),
      "c" to listOf("a")
    )

    val result = executor.isFeasible(
      participants = participants,
      exclusions = exclusions
    )

    assertThat(result).isFalse()
  }

  @Test
  fun `support mutual assignments when they are the only feasible solution`() {
    val participants = listOf("a", "b")
    val exclusions = emptyMap<String, List<String>>()

    val result = executor.executeOrThrow(
      participants = participants,
      exclusions = exclusions
    )

    assertThat(result).containsExactly(
      "a", "b",
      "b", "a"
    )
  }

  @Test
  fun `respect explicit mutual exclusions`() {
    val participants = listOf("a", "b", "c", "d")
    val exclusions = mapOf(
      "a" to listOf("b"),
      "b" to listOf("a")
    )

    val result = executor.executeOrThrow(
      participants = participants,
      exclusions = exclusions
    )

    assertValidDraw(
      participants = participants,
      exclusions = exclusions,
      draw = result
    )

    assertThat(result["a"]).isNotEqualTo("b")
    assertThat(result["b"]).isNotEqualTo("a")
  }

  @Test
  fun `produce a permutation of receivers`() {
    val participants = listOf("a", "b", "c", "d", "e")

    val result = executor.executeOrThrow(
      participants = participants,
      exclusions = emptyMap()
    )

    assertThat(result.keys).containsExactlyElementsIn(participants)
    assertThat(result.values.toSet()).containsExactlyElementsIn(participants)
  }

  private fun assertValidDraw(
    participants: List<String>,
    exclusions: Map<String, List<String>>,
    draw: Map<String, String>
  ) {
    assertThat(draw.keys).containsExactlyElementsIn(participants)
    assertThat(draw.values.toSet()).containsExactlyElementsIn(participants)
    assertThat(draw.values).hasSize(participants.size)

    draw.forEach { (giver, receiver) ->
      assertThat(giver).isNotEqualTo(receiver)
      assertThat(exclusions[giver].orEmpty()).doesNotContain(receiver)
    }
  }
}