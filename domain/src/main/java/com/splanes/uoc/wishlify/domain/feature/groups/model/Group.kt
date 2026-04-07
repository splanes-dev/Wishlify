package com.splanes.uoc.wishlify.domain.feature.groups.model

sealed class Group(
  open val id: String,
  open val name: String,
  open val photoUrl: String?,
  open val state: State,
) {
  abstract val membersCount: Int

  val isInactive get() = state == State.Inactive

  data class Basic(
    override val id: String,
    override val name: String,
    override val photoUrl: String?,
    override val state: State,
    val members: List<String>
  ) : Group(
    id = id,
    name = name,
    photoUrl = photoUrl,
    state = state,
  ) {
    override val membersCount: Int
      get() = members.count()
  }

  enum class State {
    Active,
    Inactive
  }
}
