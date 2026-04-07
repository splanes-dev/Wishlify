package com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.mapper

import android.content.Context
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupFormErrors
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.GroupsNewGroupUiFormErrors
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.MembersNewGroupFormError
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.NameNewGroupFormError
import com.splanes.uoc.wishlify.presentation.feature.groups.feature.list.creation.model.NewGroupFormError

class GroupsNewGroupFormErrorMapper(private val context: Context) {

  fun map(errors: GroupsNewGroupFormErrors): GroupsNewGroupUiFormErrors =
    GroupsNewGroupUiFormErrors(
      nameError = errors.nameError?.let(::map),
      membersError = errors.membersError?.let(::map),
    )

  private fun map(error: NewGroupFormError): String? {
    val resources = context.resources
    return when (error) {
      MembersNewGroupFormError.MembersCount ->
        resources.getString(R.string.groups_new_group_members_input_error)

      NameNewGroupFormError.Blank ->
        resources.getString(R.string.input_error_mandatory)

      NameNewGroupFormError.Length ->
        resources.getString(R.string.input_error_length, 3, 30)
    }
  }
}