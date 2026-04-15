package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model

enum class SecretSantaNewEventStep(val order: Int) {
  Basics(order = 0),
  Participants(order = 1),
  Exclusions(order = 2),
  ;

  fun next() = entries
    .sortedBy { it.order }
    .find { step -> step.order > order } ?: this

  fun previous() = entries
    .sortedByDescending { it.order }
    .find { step -> step.order < order } ?: this
}