package com.splanes.uoc.wishlify.domain.feature.groups.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaRequest

data class CreateGroupRequest(
  val id: String,
  val image: ImageMediaRequest?,
  val name: String,
  val members: List<String>,
)