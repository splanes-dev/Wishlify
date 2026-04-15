package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventBudgetFormError
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventDeadlineFormError
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventExclusionsFormError
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventFormError
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventFormErrors
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventFormUiErrors
import com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.list.creation.model.SecretSantaNewEventNameFormError

class SecretSantaNewEventFormErrorMapper(private val context: Context) {

  fun map(errors: SecretSantaNewEventFormErrors): SecretSantaNewEventFormUiErrors =
    SecretSantaNewEventFormUiErrors(
      name = errors.name?.run(::map),
      budget = errors.budget?.run(::map),
      deadline = errors.deadline?.run(::map),
      exclusions = errors.exclusions?.run(::map)
    )

  private fun map(error: SecretSantaNewEventFormError): String {
    val resources = context.resources
    return when (error) {
      SecretSantaNewEventBudgetFormError.Invalid ->
        resources.getString(R.string.secret_santa_new_event_budget_input_error)

      SecretSantaNewEventDeadlineFormError.Invalid ->
        resources.getString(R.string.secret_santa_new_event_deadline_input_error)

      SecretSantaNewEventExclusionsFormError.Invalid ->
        resources.getString(R.string.secret_santa_new_event_exclusions_input_error)

      SecretSantaNewEventNameFormError.Length ->
        resources.getString(R.string.input_error_length, 3, 30)
    }
  }
}