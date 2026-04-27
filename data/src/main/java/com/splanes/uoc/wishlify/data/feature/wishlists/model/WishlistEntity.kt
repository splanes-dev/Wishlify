package com.splanes.uoc.wishlify.data.feature.wishlists.model

import com.splanes.uoc.wishlify.data.common.media.model.ImageMediaEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Serializable Firestore persistence model for a wishlist header. */
@Serializable
data class WishlistEntity(
  @SerialName("id") val id: String = "",
  @SerialName("title") val title: String = "",
  @SerialName("description") val description: String = "",
  @SerialName("photo") val photo: ImageMediaEntity = ImageMediaEntity(),
  @SerialName("type") val type: Type = Type.Own,
  @SerialName("target") val target: String? = null,
  @SerialName("category") val category: Category? = null,
  @SerialName("editorInviteLink") val editorInviteLink: String = "",
  @SerialName("editors") val editors: List<String> = emptyList(),
  @SerialName("shareStatus") val shareStatus: ShareStatus = ShareStatus.Private,
  @SerialName("sharedWishlistId") val sharedWishlistId: String? = null,
  @SerialName("createdBy") val createdBy: String = "",
  @SerialName("createdAt") val createdAt: Long = 0L,
  @SerialName("lastUpdate") val lastUpdate: UpdateMetadata = UpdateMetadata(),
) {

  /** Persisted owner-oriented type of the wishlist. */
  @Serializable
  enum class Type {
    @SerialName("own") Own,
    @SerialName("thirdParty") ThirdParty,
  }

  /** Persisted category reference stored inside the wishlist. */
  @Serializable
  data class Category(
    @SerialName("owner") val owner: String = "",
    @SerialName("id") val id: String = "",
  )

  /** Persisted sharing status of the wishlist. */
  @Serializable
  enum class ShareStatus {
    @SerialName("private") Private,
    @SerialName("shared") Shared,
  }

  /** Persisted metadata about the last wishlist update. */
  @Serializable
  data class UpdateMetadata(
    @SerialName("updatedBy") val updatedBy: String = "",
    @SerialName("updatedAt") val updatedAt: Long = 0L,
  )
}
