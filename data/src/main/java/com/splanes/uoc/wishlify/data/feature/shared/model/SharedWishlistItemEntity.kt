package com.splanes.uoc.wishlify.data.feature.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Serializable Firestore model for the shared state associated with a wishlist item. */
@Serializable
data class SharedWishlistItemEntity(
  @SerialName("id") val id: String = "",
  @SerialName("item") val item: String = "",
  @SerialName("state") val state: State = State.Available,
  @SerialName("reservation") val reservation: Lock? = null,
  @SerialName("shareRequest") val shareRequest: ShareRequest? = null,
  @SerialName("purchased") val purchased: Purchased? = null,
) {

  /** Persisted collaborative state of the shared wishlist item. */
  @Serializable
  enum class State {
    @SerialName("available") Available,
    @SerialName("reserved") Reserved,
    @SerialName("purchased") Purchased,
    @SerialName("share_request") ShareRequest
  }

  /** Persisted reservation metadata for an item locked by one or more users. */
  @Serializable
  data class Lock(
    @SerialName("reservedBy") val reservedBy: String = "",
    @SerialName("reservedByGroup") val reservedByGroup: List<String> = emptyList(),
    @SerialName("reservedAt") val reservedAt: Long = 0L,
    @SerialName("expiresAt") val expiresAt: Long = 0L,
  )

  /** Persisted metadata for an item currently proposed as a split purchase. */
  @Serializable
  data class ShareRequest(
    @SerialName("requestedBy") val requestedBy: String = "",
    @SerialName("participantsRequested") val participantsRequested: Int = 0,
    @SerialName("participantsJoined") val participantsJoined: List<String> = emptyList(),
    @SerialName("requestedAt") val requestedAt: Long = 0L,
    @SerialName("expiresAt") val expiresAt: Long = 0L
  )

  /** Persisted purchase metadata for an item already marked as bought. */
  @Serializable
  data class Purchased(
    @SerialName("purchasedAt") val purchasedAt: Long = 0L,
    @SerialName("purchasedBy") val purchasedBy: String = "",
    @SerialName("purchasedByGroup") val purchasedByGroup: List<String> = emptyList(),
  )
}
