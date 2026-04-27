package com.splanes.uoc.wishlify.domain.feature.shared.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import java.util.Date

/** Item exposed inside a shared wishlist together with its collaborative state. */
data class SharedWishlistItem(
  val id: String,
  val linkedItem: LinkedItem,
  val state: State,
) {

  /** Snapshot of the original private wishlist item linked to this shared item. */
  data class LinkedItem(
    val id: String,
    val photoUrl: String?,
    val name: String,
    val store: String,
    val link: String,
    val unitPrice: Float,
    val amount: Int,
    val description: String,
    val priority: WishlistItem.Priority
  ) {
    val price get() = unitPrice * amount
  }

  /**
   * Collaborative state of a shared wishlist item.
   *
   * States are comparable so items can be ordered by interaction priority.
   */
  sealed interface State : Comparable<State> {
    val isCurrentUserParticipant: Boolean

    override fun compareTo(other: State): Int {
      return when (this) {
        Available -> if (other is Available) 0 else -1
        is Lock -> when (other) {
          is Lock -> 0
          is Purchased -> -1
          else -> 1
        }
        is Purchased -> if (other is Purchased) 0 else 1
        is ShareRequest -> when (other) {
          is ShareRequest -> 0
          is Available -> 1
          else -> -1
        }
      }
    }
  }

  /** Item with no active reservation, purchase or share request. */
  data object Available : State {
    override val isCurrentUserParticipant: Boolean = false
  }

  /** Item currently locked or reserved by a participant. */
  data class Lock(
    override val isCurrentUserParticipant: Boolean,
    val isLockedByCurrentUser: Boolean,
    val reservedBy: User.Basic,
    val reservedByGroup: List<User.Basic>,
    val reservedAt: Date,
    val expiresAt: Date
  ) : State

  /** Item currently under a collaborative share request. */
  data class ShareRequest(
    override val isCurrentUserParticipant: Boolean,
    val isRequestedByCurrentUser: Boolean,
    val requestedBy: User.Basic,
    val requestedAt: Date,
    val expiresAt: Date,
    val numOfParticipantsRequested: Int,
    val participantsJoined: List<User.Basic>,
  ) : State

  /** Item already purchased by a participant or group. */
  data class Purchased(
    override val isCurrentUserParticipant: Boolean,
    val isPurchasedByCurrentUser: Boolean,
    val purchasedBy: User.Basic,
    val purchasedByGroup: List<User.Basic>,
    val purchasedAt: Date
  ) : State
}
