package com.splanes.uoc.wishlify.data.feature.wishlists.repository

import com.splanes.uoc.wishlify.data.feature.shared.datasource.SharedWishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.shared.mapper.SharedWishlistsDataMapper
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.datasource.WishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.wishlists.mapper.WishlistsDataMapper
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Wishlist
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistType
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistItemRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.CreateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.ShareWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistItemRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.request.UpdateWishlistRequest
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class WishlistsRepositoryImpl(
  private val wishlistsRemoteDataSource: WishlistsRemoteDataSource,
  private val userRemoteDataSource: UserRemoteDataSource,
  private val sharedWishlistsRemoteDataSource: SharedWishlistsRemoteDataSource,
  private val userMapper: UserDataMapper,
  private val wishlistsMapper: WishlistsDataMapper,
  private val sharedWishlistsMapper: SharedWishlistsDataMapper,
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

  override suspend fun fetchWishlists(type: WishlistType, uid: String): Result<List<Wishlist>> =
    runCatching {
      coroutineScope {
        val wishlists = wishlistsRemoteDataSource.fetchWishlists(uid, type)

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

        // Items count fetch
        val itemsCountByIdDeferred = async {
          wishlists
            .map { w -> async { w.id to wishlistsRemoteDataSource.fetchWishlistItemsCount(w.id) } }
            .awaitAll()
            .toMap()
        }

        // Non purchased items count fetch
        val itemsNonPurchasedCountByIdDeferred = async {
          wishlists
            .map { w -> async { w.id to wishlistsRemoteDataSource.fetchWishlistItemsCount(w.id, excludePurchased = true) } }
            .awaitAll()
            .toMap()
        }

        val usersByUid = usersByUidDeferred.await()
        val categoriesById = categoriesByIdDeferred.await()
        val itemsCountById = itemsCountByIdDeferred.await()
        val itemsNonPurchasedCountById = itemsNonPurchasedCountByIdDeferred.await()

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
            numOfItemsMap = itemsCountById,
            numOfNonPurchasedItemsMap = itemsNonPurchasedCountById,
            users = relatedUsers.map(userMapper::mapToBasic)
          )
        }
      }
    }

  override suspend fun fetchWishlist(uid: String, wishlistId: String): Result<Wishlist> =
    runCatching {
      coroutineScope {
        val wishlist = wishlistsRemoteDataSource.fetchWishlist(wishlistId)

        val usersToFetch = buildList {
          addAll(wishlist.editors)
          add(wishlist.createdBy)
          add(wishlist.lastUpdate.updatedBy)
        }.distinct()

        // Users fetch
        val usersDeferred = async {
          usersToFetch
            .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
            .awaitAll()
            .filterNotNull()
        }

        // Categories fetch
        val categoryDeferred = wishlist.category?.let { (owner, id) ->
          async {
            wishlistsRemoteDataSource.fetchCategoryById(owner, id)
          }
        }

        // Items count fetch
        val itemsCountByIdDeferred = async {
          wishlist.id to wishlistsRemoteDataSource.fetchWishlistItemsCount(wishlist.id)
        }

        // Non purchased items count fetch
        val itemsNonPurchasedCountByIdDeferred = async {
          wishlist.id to wishlistsRemoteDataSource.fetchWishlistItemsCount(wishlist.id, excludePurchased = true)
        }

        val users = usersDeferred.await()
        val category = categoryDeferred?.await()
        val itemsCountById = itemsCountByIdDeferred.await()
        val itemsNonPurchasedCountById = itemsNonPurchasedCountByIdDeferred.await()

        wishlistsMapper.mapWishlist(
          uid = uid,
          entity = wishlist,
          category = category,
          numOfItemsMap = mapOf(itemsCountById),
          numOfNonPurchasedItemsMap = mapOf(itemsNonPurchasedCountById),
          users = users.map(userMapper::mapToBasic)
        )
      }
    }

  override suspend fun fetchWishlistItems(wishlistId: String): Result<List<WishlistItem>> =
    runCatching {
      coroutineScope {
        val items = wishlistsRemoteDataSource.fetchWishlistItems(wishlistId)

        val usersToFetch = items
          .flatMap { item ->
            buildList {
              add(item.createdBy)
              add(item.lastUpdate.updatedBy)
              item.purchased?.purchasedBy?.let(::add)
            }
          }.distinct()

        // Users fetch
        val usersByUidDeferred = async {
          usersToFetch
            .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
            .awaitAll()
            .filterNotNull()
            .associateBy { user -> user.uid }
        }

        val usersByUid = usersByUidDeferred.await()
        items.map { item ->
          val relatedUsers = buildList {
            usersByUid[item.createdBy]?.let(::add)
            usersByUid[item.lastUpdate.updatedBy]?.let(::add)
            item.purchased?.purchasedBy?.let(usersByUid::get)?.let(::add)
          }.distinctBy { it.uid }

          wishlistsMapper.mapItem(
            entity = item,
            users = relatedUsers.map(userMapper::mapToBasic)
          )
        }
      }
    }

  override suspend fun fetchWishlistItem(
    wishlistId: String,
    item: String
  ): Result<WishlistItem> =
    runCatching {
      coroutineScope {
        val item = wishlistsRemoteDataSource.fetchWishlistItem(wishlistId, itemId = item)

        val usersToFetch = buildList {
          add(item.createdBy)
          add(item.lastUpdate.updatedBy)
          item.purchased?.purchasedBy?.let(::add)
        }.distinct()

        // Users fetch
        val usersByUidDeferred = async {
          usersToFetch
            .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
            .awaitAll()
            .filterNotNull()
        }

        val usersByUid = usersByUidDeferred.await()

        wishlistsMapper.mapItem(
          entity = item,
          users = usersByUid.map(userMapper::mapToBasic)
        )
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

  override suspend fun updateWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: UpdateWishlistRequest
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.wishlistFromRequest(uid, imageMedia, request)
      wishlistsRemoteDataSource.upsertWishlist(entity)
    }

  override suspend fun shareWishlist(
    uid: String,
    request: ShareWishlistRequest
  ): Result<Unit> =
    runCatching {
      val sharedWishlistEntity = sharedWishlistsMapper.sharedWishlistFromRequest(request)
      val wishlistEntity = wishlistsRemoteDataSource.fetchWishlist(request.wishlistId)

      val updatedWishlist = wishlistsMapper.shareWishlist(
        uid = uid,
        sharedWishlistId = sharedWishlistEntity.id,
        entity = wishlistEntity
      )

      sharedWishlistsRemoteDataSource.upsertSharedWishlist(sharedWishlistEntity)
      wishlistsRemoteDataSource.upsertWishlist(entity = updatedWishlist)
    }

  override suspend fun deleteWishlist(wishlist: String): Result<Unit> =
    runCatching {
      wishlistsRemoteDataSource.removeWishlist(wishlistId = wishlist)
    }

  override suspend fun addWishlistItem(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateWishlistItemRequest
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.wishlistItemFromRequest(uid, imageMedia, request)
      wishlistsRemoteDataSource.upsertWishlistItem(wishlistId = request.wishlist, entity = entity)
    }

  override suspend fun updateWishlistItem(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateWishlistItemRequest
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.wishlistItemFromRequest(uid, imageMedia, request)
      wishlistsRemoteDataSource.upsertWishlistItem(wishlistId = request.wishlist, entity = entity)
    }

  override suspend fun updateCategory(
    uid: String,
    category: Category
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.mapCategory(category)
      wishlistsRemoteDataSource.upsertCategory(uid, entity)
    }

  override suspend fun deleteWishlistItem(
    wishlist: String,
    item: String
  ): Result<Unit> =
    runCatching {
      wishlistsRemoteDataSource.removeWishlistItem(wishlistId = wishlist, itemId = item)
    }

  override suspend fun deleteCategory(
    uid: String,
    category: String
  ): Result<Unit> =
    runCatching {
      wishlistsRemoteDataSource.removeCategory(uid, category)
    }
}