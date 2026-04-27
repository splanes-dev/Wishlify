package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model

/**
 * Ordered steps of the Secret Santa event creation and edition flow.
 */
enum class SecretSantaNewEventStep(val order: Int) {
  Basics(order = 0),
  Participants(order = 1),
  Exclusions(order = 2),
  ;

  /**
   * Returns the next step in the flow, or the current one if it is already the last step.
   */
  fun next() = entries
    .sortedBy { it.order }
    .find { step -> step.order > order } ?: this

  /**
   * Returns the previous step in the flow, or the current one if it is already the first step.
   */
  fun previous() = entries
    .sortedByDescending { it.order }
    .find { step -> step.order < order } ?: this
}
