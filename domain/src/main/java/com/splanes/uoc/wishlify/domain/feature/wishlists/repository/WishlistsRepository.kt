package com.splanes.uoc.wishlify.domain.feature.wishlists.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistType
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistItemRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistItemRequest

interface WishlistsRepository {
  suspend fun fetchWishlists(type: WishlistType, uid: String): Result<List<Wishlist>>
  suspend fun fetchWishlist(uid: String, wishlistId: String): Result<Wishlist>
  suspend fun fetchWishlistItems(wishlistId: String): Result<List<WishlistItem>>
  suspend fun fetchWishlistItem(wishlistId: String, item: String): Result<WishlistItem>
  suspend fun fetchCategories(uid: String): Result<List<Category>>
  suspend fun addWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: CreateWishlistRequest
  ): Result<Unit>

  suspend fun addWishlistItem(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateWishlistItemRequest
  ): Result<Unit>

  suspend fun updateWishlistItem(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateWishlistItemRequest
  ): Result<Unit>

  suspend fun deleteWishlistItem(
    wishlist: String,
    item: String
  ): Result<Unit>

  suspend fun addCategory(uid: String, category: Category): Result<Unit>

  suspend fun updateCategory(uid: String, category: Category): Result<Unit>

  suspend fun deleteCategory(uid: String, category: String): Result<Unit>
}