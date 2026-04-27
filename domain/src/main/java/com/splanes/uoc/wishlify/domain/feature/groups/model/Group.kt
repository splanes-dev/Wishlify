package com.splanes.uoc.wishlify.domain.feature.groups.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User

/**
 * Domain representation of a user group used to organize collaborative flows.
 *
 * Groups may be returned either as a lightweight list item or as a richer
 * detail model including resolved user information.
 */
sealed class Group(
  open val id: String,
  open val name: String,
  open val photoUrl: String?,
  open val state: State,
) {
  abstract val membersCount: Int
  abstract val membersUid: List<String>

  /** Whether the group is currently not linked to active collaborative activity. */
  val isInactive get() = state == State.Inactive

  /**
   * Lightweight group projection used in listings.
   */
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

  /**
   * Detailed group projection with resolved member information and activity flags.
   *
   * The state is derived from whether the group is linked to shared wishlists
   * or Secret Santa events.
   */
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

  /**
   * Activity state of a group within the application.
   */
  enum class State {
    Active,
    Inactive
  }
}
