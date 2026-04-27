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

/** Repository contract for wishlists, items, categories and link metadata extraction. */
interface WishlistsRepository {
  /** Retrieves the wishlists visible to the given user. */
  suspend fun fetchWishlists(uid: String): Result<List<Wishlist>>
  /** Retrieves a single wishlist for the given user. */
  suspend fun fetchWishlist(uid: String, wishlistId: String): Result<Wishlist>
  /** Retrieves all items of a wishlist. */
  suspend fun fetchWishlistItems(wishlistId: String): Result<List<WishlistItem>>
  /** Retrieves a single item from a wishlist. */
  suspend fun fetchWishlistItem(wishlistId: String, item: String): Result<WishlistItem>
  /** Retrieves the categories available to the given user. */
  suspend fun fetchCategories(uid: String): Result<List<Category>>
  /** Creates a new wishlist. */
  suspend fun addWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: CreateWishlistRequest
  ): Result<Unit>

  /** Persists the updated state of an existing wishlist. */
  suspend fun updateWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: UpdateWishlistRequest
  ): Result<Unit>

  /** Shares a private wishlist with other participants. */
  suspend fun shareWishlist(
    uid: String,
    request: ShareWishlistRequest
  ): Result<Unit>

  /** Deletes a wishlist. */
  suspend fun deleteWishlist(wishlist: String): Result<Unit>

  /** Adds a new item to a wishlist. */
  suspend fun addWishlistItem(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateWishlistItemRequest
  ): Result<Unit>

  /** Updates an existing wishlist item. */
  suspend fun updateWishlistItem(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateWishlistItemRequest
  ): Result<Unit>

  /** Deletes a wishlist item. */
  suspend fun deleteWishlistItem(
    wishlist: String,
    item: String
  ): Result<Unit>

  /** Creates a new category for the given user. */
  suspend fun addCategory(uid: String, category: Category): Result<Unit>

  /** Updates an existing category for the given user. */
  suspend fun updateCategory(uid: String, category: Category): Result<Unit>

  /** Deletes a category for the given user. */
  suspend fun deleteCategory(uid: String, category: String): Result<Unit>

  /** Extracts product metadata from a URL using the primary backend strategy. */
  suspend fun extractUrlData(url: String): Result<WishlistItemUrlData>

  /** Completes or refines extracted URL metadata using a local fallback strategy. */
  suspend fun extractUrlDataLocally(data: WishlistItemUrlData, url: String): Result<WishlistItemUrlData>

  /** Adds the current user as editor using an invitation token. */
  suspend fun addWishlistEditor(token: String): Result<Unit>
}
