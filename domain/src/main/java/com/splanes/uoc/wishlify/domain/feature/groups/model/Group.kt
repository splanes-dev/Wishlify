package com.splanes.uoc.wishlify.domain.feature.groups.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User

sealed class Group(
  open val id: String,
  open val name: String,
  open val photoUrl: String?,
  open val state: State,
) {
  abstract val membersCount: Int
  abstract val membersUid: List<String>

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

    override val membersUid: List<String>
      get() = members
  }

  data class Detail(
    override val id: String,
    override val name: String,
    override val photoUrl: String?,
    val members: List<User.Basic>,
    val currentUserUid: String,
    val hasSharedWishlists: Boolean,
    val hasSecretSantaEvents: Boolean,
  ) : Group(
    id = id,
    name = name,
    photoUrl = photoUrl,
    state = if (hasSharedWishlists || hasSecretSantaEvents) State.Active else State.Inactive,
  ) {
    override val membersCount: Int
      get() = members.count()

    override val membersUid: List<String>
      get() = members.map { it.uid }
  }

  enum class State {
    Active,
    Inactive
  }
}
