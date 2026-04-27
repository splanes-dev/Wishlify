package com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.navigation

import kotlinx.serialization.Serializable

/** Root route of the groups navigation graph. */
@Serializable
data object Groups {

  /** Route of the groups list screen. */
  @Serializable
  data object List

  /** Route of the group creation flow. */
  @Serializable
  data object NewGroup

  /** Route of the group edition flow. */
  @Serializable
  data class EditGroup(
    val groupId: String,
    val groupName: String,
  )

  /** Route of the user-search flow used to add members into a group form. */
  @Serializable
  data object SearchUsers

  /** Route of the group detail screen. */
  @Serializable
  data class Detail(
    val groupId: String,
    val groupName: String,
  )
}
