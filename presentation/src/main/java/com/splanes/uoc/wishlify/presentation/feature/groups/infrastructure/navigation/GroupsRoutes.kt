package com.splanes.uoc.wishlify.presentation.feature.groups.infrastructure.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Groups {

  @Serializable
  data object List

  @Serializable
  data object NewGroup

  @Serializable
  data class EditGroup(
    val groupId: String,
    val groupName: String,
  )

  @Serializable
  data object SearchUsers

  @Serializable
  data class Detail(
    val groupId: String,
    val groupName: String,
  )
}