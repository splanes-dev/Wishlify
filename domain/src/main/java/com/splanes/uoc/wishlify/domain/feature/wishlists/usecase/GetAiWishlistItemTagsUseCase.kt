package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.ia.repository.AiRepository

class GetAiWishlistItemTagsUseCase(
  private val aiRepository: AiRepository
) : UseCase() {

  suspend operator fun invoke(name: String, tags: String) = execute {

    val existingTags = tags
      .split(",")
      .map { it.trim().lowercase() }
      .filter { it.isNotBlank() }

    val availableSlots = 3 - existingTags.size
    if (availableSlots <= 0) {
      Result.success(emptyList())
    } else {
      val context = buildContext(name, existingTags)
      runCatching {
        aiRepository
          .getWishlistItemTags(context = context)
          .filterNot { suggested ->
            existingTags.any { it.equals(suggested.name, ignoreCase = true) }
          }
      }
    }
  }

  private fun buildContext(
    name: String,
    currentTags: List<String>,
  ): String {
    return buildString {
      append("producte: ")
      append(name)
      if (currentTags.isNotEmpty()) {
        append(". tags actuals: ")
        append(currentTags.joinToString(", "))
      }
    }
  }
}