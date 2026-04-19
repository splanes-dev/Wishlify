package com.splanes.uoc.wishlify.domain.feature.secretsanta.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.secretsanta.helper.SecretSantaDrawExecutor
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secretsanta.repository.SecretSantaRepository
import com.splanes.uoc.wishlify.domain.feature.session.usecase.GetCurrentUserIdUseCase

class DoSecretSantaDrawUseCase(
  private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
  private val executor: SecretSantaDrawExecutor,
  private val repository: SecretSantaRepository
) : UseCase() {

  suspend operator fun invoke(event: SecretSantaEventDetail) = execute {
    getCurrentUserIdUseCase()
      .mapCatching { uid ->

        val participants = buildList {
          event.createdBy.uid.run(::add)
          event.participants.map { p -> p.uid }.run(::addAll)
          event.group?.members?.run(::addAll)
        }.distinct()

        val exclusions = event.exclusions
          .mapKeys { (user, _) -> user.uid }
          .mapValues { (_, users) -> users.map { user -> user.uid } }

        val assignments = executor.executeOrThrow(
          participants = participants,
          exclusions = exclusions
        )

        repository.doSecretSantaDraw(uid, event.id, assignments).getOrThrow()
      }
  }
}