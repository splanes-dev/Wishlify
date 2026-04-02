package com.splanes.uoc.wishlify.domain.feature.wishlists.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist

interface WishlistsRepository {
  suspend fun fetchWishlists(uid: String): Result<List<Wishlist>>
  suspend fun fetchCategories(uid: String): Result<List<Category>>
  suspend fun addWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: CreateWishlistRequest
  ): Result<Unit>

  suspend fun addCategory(uid: String, category: Category): Result<Unit>
}