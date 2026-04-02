package com.splanes.uoc.wishlify.data.feature.wishlists.repository

import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.datasource.WishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.wishlists.mapper.WishlistsDataMapper
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class WishlistsRepositoryImpl(
  private val wishlistsRemoteDataSource: WishlistsRemoteDataSource,
  private val userRemoteDataSource: UserRemoteDataSource,
  private val userMapper: UserDataMapper,
  private val wishlistsMapper: WishlistsDataMapper
) : WishlistsRepository {

  override suspend fun fetchCategories(uid: String): Result<List<Category>> =
    runCatching {
      wishlistsRemoteDataSource.fetchCategories(uid)
    }.map { categories ->
      categories.map(wishlistsMapper::mapCategory)
    }

  override suspend fun addCategory(uid: String, category: Category): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.mapCategory(category)
      wishlistsRemoteDataSource.upsertCategory(uid, entity)
    }

  override suspend fun fetchWishlists(uid: String): Result<List<Wishlist>> =
    runCatching {
      coroutineScope {
        val wishlists = wishlistsRemoteDataSource.fetchWishlists(uid)

        val usersToFetch = wishlists
          .flatMap { wishlist ->
            buildList {
              addAll(wishlist.editors)
              add(wishlist.createdBy)
              add(wishlist.lastUpdate.updatedBy)
            }
          }.distinct()

        val categoriesToFetch = wishlists
          .mapNotNull { wishlist -> wishlist.category }
          .distinctBy { category -> category.id }

        // Users fetch
        val usersByUidDeferred = async {
          usersToFetch
            .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
            .awaitAll()
            .filterNotNull()
            .associateBy { user -> user.uid }
        }

        // Categories fetch
        val categoriesByIdDeferred = async {
          categoriesToFetch
            .map { (owner, id) ->
              async { wishlistsRemoteDataSource.fetchCategoryById(owner, id) }
            }
            .awaitAll()
            .filterNotNull()
            .associateBy { category -> category.id }
        }

        val usersByUid = usersByUidDeferred.await()
        val categoriesById = categoriesByIdDeferred.await()

        wishlists.map { wishlist ->
          val category = wishlist.category?.id?.let(categoriesById::get)

          val relatedUsers = buildList {
            wishlist.editors.forEach { userId ->
              usersByUid[userId]?.let(::add)
            }
            usersByUid[wishlist.createdBy]?.let(::add)
            usersByUid[wishlist.lastUpdate.updatedBy]?.let(::add)
          }.distinctBy { it.uid }

          wishlistsMapper.mapWishlist(
            uid = uid,
            entity = wishlist,
            category = category,
            users = relatedUsers.map(userMapper::mapToBasic)
          )
        }
      }
    }

  override suspend fun addWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: CreateWishlistRequest
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.wishlistFromRequest(uid, imageMedia, request)
      wishlistsRemoteDataSource.upsertWishlist(entity)
    }
}