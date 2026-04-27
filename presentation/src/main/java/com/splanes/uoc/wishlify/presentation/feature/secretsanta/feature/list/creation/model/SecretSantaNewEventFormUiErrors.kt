package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model

/**
 * UI-ready validation messages for the Secret Santa event form.
 */
data class SecretSantaNewEventFormUiErrors(
  val name: String? = null,
  val budget: String? = null,
  val deadline: String? = null,
  val exclusions: String? = null
)

/**
 * Typed validation errors produced while validating the Secret Santa event form.
 */
data class SecretSantaNewEventFormErrors(
  val name: SecretSantaNewEventNameFormError? = null,
  val budget: SecretSantaNewEventBudgetFormError? = null,
  val deadline: SecretSantaNewEventDeadlineFormError? = null,
  val exclusions: SecretSantaNewEventExclusionsFormError? = null
)

/**
 * Marker interface for Secret Santa event form validation errors.
 */
sealed interface SecretSantaNewEventFormError

/**
 * Validation errors for the event name field.
 */
sealed interface SecretSantaNewEventNameFormError : SecretSantaNewEventFormError {
  data object Length : SecretSantaNewEventNameFormError
}

/**
 * Validation errors for the event budget field.
 */
sealed interface SecretSantaNewEventBudgetFormError : SecretSantaNewEventFormError {
  data object Invalid : SecretSantaNewEventBudgetFormError
}

/**
 * Validation errors for the event deadline field.
 */
sealed interface SecretSantaNewEventDeadlineFormError : SecretSantaNewEventFormError {
  data object Invalid : SecretSantaNewEventDeadlineFormError
}

/**
 * Validation errors for the exclusions step.
 */
sealed interface SecretSantaNewEventExclusionsFormError : SecretSantaNewEventFormError {
  data object Invalid : SecretSantaNewEventExclusionsFormError
}
