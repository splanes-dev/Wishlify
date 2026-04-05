package com.splanes.uoc.wishlify.domain.feature.wishlists.model

import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.util.Date

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

  val price
    get() = unitPrice * amount

  enum class Priority {
    Standard,
    Top,
    Supertop,
  }

  data class UpdateMetadata(
    val updatedBy: User.Basic,
    val updatedAt: Date
  )

  data class PurchaseMetadata(
    val purchasedBy: User.Basic,
    val purchasedAt: Date
  )
}