package com.splanes.uoc.wishlify.domain.feature.wishlists.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.util.Date

/** Item stored inside a wishlist. */
data class WishlistItem(
  val id: String,
  val photoUrl: String?,
  val name: String,
  val description: String,
  val store: String,
  val unitPrice: Float,
  val amount: Int,
  val priority: Priority,
  val link: String,
  val tags: List<String>,
  val createdBy: User.Basic,
  val createdAt: Date,
  val lastUpdate: UpdateMetadata,
  val purchased: PurchaseMetadata?
) {

  /** Total price based on unit price and requested amount. */
  val price
    get() = unitPrice * amount

  /** Relative priority of a wishlist item. */
  enum class Priority(val weight: Int) {
    Standard(0),
    Top(1),
    Supertop(2),
  }

  /** Metadata about the latest update performed on the item. */
  data class UpdateMetadata(
    val updatedBy: User.Basic,
    val updatedAt: Date
  )

  /** Purchase metadata when the item has already been bought. */
  data class PurchaseMetadata(
    val purchasedBy: User.Basic,
    val purchasedAt: Date
  )
}
