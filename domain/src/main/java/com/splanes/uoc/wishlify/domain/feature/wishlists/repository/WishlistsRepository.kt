package com.splanes.uoc.wishlify.domain.feature.wishlists.repository

import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItemUrlData
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistItemRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.ShareWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistItemRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistRequest

interface WishlistsRepository {
  suspend fun fetchWishlists(uid: String): Result<List<Wishlist>>
  suspend fun fetchWishlist(uid: String, wishlistId: String): Result<Wishlist>
  suspend fun fetchWishlistItems(wishlistId: String): Result<List<WishlistItem>>
  suspend fun fetchWishlistItem(wishlistId: String, item: String): Result<WishlistItem>
  suspend fun fetchCategories(uid: String): Result<List<Category>>
  suspend fun addWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: CreateWishlistRequest
  ): Result<Unit>

  suspend fun updateWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: UpdateWishlistRequest
  ): Result<Unit>

  suspend fun shareWishlist(
    uid: String,
    request: ShareWishlistRequest
  ): Result<Unit>

  suspend fun deleteWishlist(wishlist: String): Result<Unit>

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

  suspend fun extractUrlData(url: String): Result<WishlistItemUrlData>

  suspend fun extractUrlDataLocally(data: WishlistItemUrlData, url: String): Result<WishlistItemUrlData>

  suspend fun addWishlistEditor(uid: String, token: String): Result<Unit>
}