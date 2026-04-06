package com.splanes.uoc.wishlify.domain.feature.groups.model

sealed class Group(
  open val id: String,
  open val name: String,
  open val photoUrl: String?,
) {
  abstract val membersCount: Int

  data class Basic(
    override val id: String,
    override val name: String,
    override val photoUrl: String?,
    val members: List<String>
  ) : Group(
    id = id,
    name = name,
    photoUrl = photoUrl
  ) {
    override val membersCount: Int
      get() = members.count()
  }
}
