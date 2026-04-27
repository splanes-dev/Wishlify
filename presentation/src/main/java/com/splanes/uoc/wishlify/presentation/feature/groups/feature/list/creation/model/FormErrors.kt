package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model

/** Marker for the validation errors that can appear in the group form. */
sealed interface NewGroupFormError

/** Validation errors associated with the group name input. */
sealed interface NameNewGroupFormError : NewGroupFormError {
  data object Blank : NameNewGroupFormError
  data object Length : NameNewGroupFormError
}

/** Validation errors associated with the group members selection. */
sealed interface MembersNewGroupFormError : NewGroupFormError {
  data object MembersCount : MembersNewGroupFormError
}
