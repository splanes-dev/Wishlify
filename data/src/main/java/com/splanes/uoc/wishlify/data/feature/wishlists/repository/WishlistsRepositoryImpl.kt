package com.splanes.uoc.wishlify.data.feature.wishlists.repository

import com.splanes.uoc.wishlify.data.feature.wishlists.datasource.WishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.wishlists.mapper.WishlistsDataMapper
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import java.io.File

class WishlistsRepositoryImpl(
  private val wishlistsRemoteDataSource: WishlistsRemoteDataSource,
  private val mapper: WishlistsDataMapper
) : WishlistsRepository {

  override suspend fun fetchCategories(uid: String): Result<List<Category>> =
    runCatching {
      wishlistsRemoteDataSource.fetchCategories(uid)
    }.map { categories ->
      categories.map(mapper::mapCategory)
    }

  override suspend fun addCategory(uid: String, category: Category): Result<Unit> =
    runCatching {
      val entity = mapper.mapCategory(category)
      wishlistsRemoteDataSource.upsertCategory(uid, entity)
    }

  override suspend fun addWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: CreateWishlistRequest
  ): Result<Unit> =
   runCatching {
    val entity = mapper.wishlistFromRequest(uid, imageMedia, request)
    wishlistsRemoteDataSource.upsertWishlist(entity)
  }

  private fun wishlistCoverPath(id: String): String =
    WISHLIST_COVER_PATH.format(id)

  private fun wishlistCoverFilename(file: File): String =
    WISHLIST_COVER_FILENAME.format(file.extension)
}

private const val WISHLIST_COVER_PATH = "wishlists/%s/cover"
private const val WISHLIST_COVER_FILENAME = "cover.%s"