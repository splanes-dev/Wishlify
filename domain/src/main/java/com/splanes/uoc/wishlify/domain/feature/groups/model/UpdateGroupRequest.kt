package com.splanes.uoc.wishlify.domain.feature.groups.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest

/**
 * Input required to update an existing group.
 */
data class UpdateGroupRequest(
  val id: String,
  val name: String,
  val members: List<String>,
  val image: ImageMediaRequest?,
  val includeCurrentUser: Boolean,
)
