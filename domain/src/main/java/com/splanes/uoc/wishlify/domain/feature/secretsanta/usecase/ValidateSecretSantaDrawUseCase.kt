package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.helper.SecretSantaDrawExecutor
import com.splanes.uoc.wishlify.domain.feature.user.model.User

/** Checks whether a Secret Santa draw is feasible for the given participants and exclusions. */
class ValidateSecretSantaDrawUseCase(
  private val executor: SecretSantaDrawExecutor
) : UseCase() {

  /** Returns whether a valid draw can be produced. */
  operator fun invoke(
    participants: List<User.Basic>,
    exclusions: List<Pair<User.Basic, User.Basic>>
  ): Boolean {

    val participantsIds = participants.map { it.uid }
    val exclusionsMap = exclusions
      .map { (m1, m2) -> m1.uid to m2.uid }
      .groupBy(
        keySelector = { (m1, _) -> m1 },
        valueTransform = { (_, m2) -> m2 }
      )

    return executor.isFeasible(participantsIds, exclusionsMap)
  }
}
