package com.splanes.uoc.wishlify.domain.feature.groups.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest

data class UpdateGroupRequest(
  val id: String,
  val name: String,
  val members: List<String>,
  val image: ImageMediaRequest?,
  val includeCurrentUser: Boolean,
)