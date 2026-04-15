package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model

data class SecretSantaNewEventFormUiErrors(
  val name: String? = null,
  val budget: String? = null,
  val deadline: String? = null,
  val exclusions: String? = null
)

data class SecretSantaNewEventFormErrors(
  val name: SecretSantaNewEventNameFormError? = null,
  val budget: SecretSantaNewEventBudgetFormError? = null,
  val deadline: SecretSantaNewEventDeadlineFormError? = null,
  val exclusions: SecretSantaNewEventExclusionsFormError? = null
)

sealed interface SecretSantaNewEventFormError

sealed interface SecretSantaNewEventNameFormError : SecretSantaNewEventFormError {
  data object Length : SecretSantaNewEventNameFormError
}

sealed interface SecretSantaNewEventBudgetFormError : SecretSantaNewEventFormError {
  data object Invalid : SecretSantaNewEventBudgetFormError
}

sealed interface SecretSantaNewEventDeadlineFormError : SecretSantaNewEventFormError {
  data object Invalid : SecretSantaNewEventDeadlineFormError
}

sealed interface SecretSantaNewEventExclusionsFormError : SecretSantaNewEventFormError {
  data object Invalid : SecretSantaNewEventExclusionsFormError
}