package com.splanes.uoc.wishlify.domain.feature.shared.model

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.time.Instant
import java.util.Date

/**
 * Domain representation of a shared wishlist.
 *
 * It may represent either a wishlist owned by the current user or one shared
 * by a third party.
 */
sealed class SharedWishlist(
  open val id: String,
  open val linkedWishlist: LinkedWishlist,
  open val owner: User.Basic,
  open val group: Group.Basic?,
  open val participants: List<User.Basic>,
  open val editors: List<User.Basic>,
  open val inviteLink: InviteLink,
  open val deadline: Date,
  open val sharedAt: Date,
  open val numOfItems: Int,
) {

  /** Whether the shared wishlist deadline has already passed. */
  fun isFinished() =
    deadline.toInstant().isBefore(Instant.now())

  /** Shared wishlist owned by the current user. */
  data class Own(
    override val id: String,
    override val linkedWishlist: LinkedWishlist,
    override val owner: User.Basic,
    override val group: Group.Basic?,
    override val participants: List<User.Basic>,
    override val editors: List<User.Basic>,
    override val inviteLink: InviteLink,
    override val deadline: Date,
    override val sharedAt: Date,
    override val numOfItems: Int,
  ) : SharedWishlist(
    id = id,
    linkedWishlist = linkedWishlist,
    owner = owner,
    group = group,
    participants = participants,
    editors = editors,
    inviteLink = inviteLink,
    deadline = deadline,
    sharedAt = sharedAt,
    numOfItems = numOfItems,
  )

  /** Shared wishlist owned by another user and visible to the current user. */
  data class ThirdParty(
    override val id: String,
    override val linkedWishlist: LinkedWishlist,
    override val owner: User.Basic,
    override val group: Group.Basic?,
    override val participants: List<User.Basic>,
    override val editors: List<User.Basic>,
    override val inviteLink: InviteLink,
    override val deadline: Date,
    override val sharedAt: Date,
    override val numOfItems: Int,
    val target: String,
    val pendingNotificationsCount: Int,
    val editorsCanSeeUpdates: Boolean,
  ) : SharedWishlist(
    id = id,
    linkedWishlist = linkedWishlist,
    owner = owner,
    group = group,
    participants = participants,
    editors = editors,
    inviteLink = inviteLink,
    deadline = deadline,
    sharedAt = sharedAt,
    numOfItems = numOfItems,
  ) {
    /** Returns the number of unique participants that can effectively interact with updates. */
    fun totalParticipantsCount(): Int {
      val participantsUid = participants.map { it.uid }
      val editorsUid = editors.map { it.uid }
      return buildList {
        group?.members?.let(::addAll)
        addAll(participantsUid)
        addAll(editorsUid)
      }.distinct().count { uid -> editorsCanSeeUpdates || uid !in editorsUid }
    }
  }

  /** Lightweight representation of the private wishlist linked to the shared flow. */
  data class LinkedWishlist(
    val id: String,
    val name: String,
    val photo: ImageMedia,
    val target: String?,
    val description: String,
  )
}
