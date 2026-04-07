package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model

sealed interface NewGroupFormError

sealed interface NameNewGroupFormError : NewGroupFormError {
  data object Blank : NameNewGroupFormError
  data object Length : NameNewGroupFormError
}

sealed interface MembersNewGroupFormError : NewGroupFormError {
  data object MembersCount : MembersNewGroupFormError
}