package com.splanes.uoc.wishlify.presentation.feature.shared.infrastructure.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SharedWishlists {

  @Serializable
  data object List

  @Serializable
  data class ThirdPartyDetail(
    val sharedWishlistId: String,
    val sharedWishlistName: String,
    val target: String
  )

  @Serializable
  data class OwnDetail(val sharedWishlistId: String, val sharedWishlistName: String)
}