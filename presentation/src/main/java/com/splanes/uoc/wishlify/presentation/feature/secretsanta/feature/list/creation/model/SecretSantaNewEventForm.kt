package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model

import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.presentation.common.components.ImagePicker

/**
 * Form data collected during Secret Santa event creation and edition.
 */
data class SecretSantaNewEventForm(
  val photo: ImagePicker.Resource? = null,
  val name: String = "",
  val budget: Double = 0.0,
  val isBudgetApproximate: Boolean = false,
  val deadline: Long = 0L,
  val group: Group.Basic? = null,
  val participants: List<User.Basic> = emptyList(),
  val exclusions: List<Pair<User.Basic, User.Basic>> = emptyList()
) {
  /**
   * Inputs whose validation errors can be cleared independently.
   */
  enum class Input {
    Name,
    Budget,
    Deadline,
    Exclusions,
  }
}
