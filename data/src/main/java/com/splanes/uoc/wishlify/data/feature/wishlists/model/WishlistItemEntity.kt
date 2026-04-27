package com.splanes.uoc.wishlify.data.feature.wishlists.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Serializable Firestore persistence model for a wishlist item. */
@Serializable
data class WishlistItemEntity(
  @SerialName("id") val id: String = "",
  @SerialName("photoUrl") val photoUrl: String? = null,
  @SerialName("name") val name: String = "",
  @SerialName("description") val description: String? = null,
  @SerialName("store") val store: String? = null,
  @SerialName("unitPrice") val unitPrice: Float = 0f,
  @SerialName("amount") val amount: Int = 0,
  @SerialName("priority") val priority: Priority = Priority.Standard,
  @SerialName("link") val link: String? = null,
  @SerialName("tags") val tags: List<String> = emptyList(),
  @SerialName("createdBy") val createdBy: String = "",
  @SerialName("createdAt") val createdAt: Long = 0L,
  @SerialName("lastUpdate") val lastUpdate: UpdateMetadata = UpdateMetadata(),
  @SerialName("purchased") val purchased: PurchaseMetadata? = null
) {

  /** Persisted display priority of the wishlist item. */
  @Serializable
  enum class Priority {
    @SerialName("standard") Standard,
    @SerialName("top") Top,
    @SerialName("supertop") Supertop,
  }

  /** Persisted metadata about the last item update. */
  @Serializable
  data class UpdateMetadata(
    @SerialName("updatedBy") val updatedBy: String = "",
    @SerialName("updatedAt") val updatedAt: Long = 0L,
  )

  /** Persisted purchase metadata when the item has been marked as bought. */
  @Serializable
  data class PurchaseMetadata(
    @SerialName("purchasedBy") val purchasedBy: String = "",
    @SerialName("purchasedAt") val purchasedAt: Long = 0L,
  )
}
