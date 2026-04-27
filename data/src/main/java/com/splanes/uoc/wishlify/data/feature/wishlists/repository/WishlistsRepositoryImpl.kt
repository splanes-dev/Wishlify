package com.splanes.uoc.wishlify.data.feature.wishlists.repository

import com.splanes.uoc.wishlify.data.common.utils.nowInMillis
import com.splanes.uoc.wishlify.data.feature.groups.datasource.GroupsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.secretsanta.datasource.SecretSantaRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.shared.datasource.SharedWishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.shared.mapper.SharedWishlistsDataMapper
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.datasource.WishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.wishlists.mapper.WishlistsDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.util.UrlDataExtractor
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
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Data-layer implementation of [WishlistsRepository].
 *
 * It composes wishlist persistence with users, shared wishlists, Secret Santa
 * links and metadata extraction helpers to return the richer domain models
 * required by the app.
 */
class WishlistsRepositoryImpl(
  private val wishlistsRemoteDataSource: WishlistsRemoteDataSource,
  private val userRemoteDataSource: UserRemoteDataSource,
  private val groupsRemoteDataSource: GroupsRemoteDataSource,
  private val sharedWishlistsRemoteDataSource: SharedWishlistsRemoteDataSource,
  private val secretSantaRemoteDataSource: SecretSantaRemoteDataSource,
  private val userMapper: UserDataMapper,
  private val wishlistsMapper: WishlistsDataMapper,
  private val sharedWishlistsMapper: SharedWishlistsDataMapper,
  private val urlDataExtractor: UrlDataExtractor,
) : WishlistsRepository {

  /** Fetches and maps the personal categories owned by the user. */
  override suspend fun fetchCategories(uid: String): Result<List<Category>> =
    runCatching {
      wishlistsRemoteDataSource.fetchCategories(uid)
    }.map { categories ->
      categories.map(wishlistsMapper::mapCategory)
    }

  /** Persists a personal category for the user. */
  override suspend fun addCategory(uid: String, category: Category): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.mapCategory(category)
      wishlistsRemoteDataSource.upsertCategory(uid, entity)
    }

  /**
   * Fetches the user's visible wishlists and enriches them with users,
   * categories, item counts and active shared or Secret Santa linkage.
   */
  override suspend fun fetchWishlists(uid: String): Result<List<Wishlist>> =
    runCatching {
      coroutineScope {
        val wishlists = wishlistsRemoteDataSource.fetchWishlists(uid)

        val sharedWishlistsToFetch = wishlists
          .filter { wishlist -> wishlist.shareStatus == WishlistEntity.ShareStatus.Shared }
          .mapNotNull { wishlist -> wishlist.sharedWishlistId }
          .distinct()

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

        // SharedWishlists fetch
        val sharedWishlistsDeferred = async {
          sharedWishlistsToFetch
            .map { id -> async { sharedWishlistsRemoteDataSource.fetchSharedWishlistById(id) } }
            .awaitAll()
            .filterNotNull()
            .filter { w -> !w.editorsCanSeeUpdates }
            .associateBy { shared -> shared.wishlist }
        }

        // SecretSantaEvents fetch
        val secretSantaEventsDeferred = async {
          val groups = groupsRemoteDataSource.fetchGroups(uid)
          secretSantaRemoteDataSource
            .fetchSecretSantaEvents(uid, groups.map { it.id })
            .filter { event -> event.deadline > nowInMillis() } // Non-expired
            .map { event ->
              async {
                val w = secretSantaRemoteDataSource.fetchParticipantWishlist(event.id, uid)
                w?.let { w.wishlist to event }
              }
            }
            .awaitAll()
            .filterNotNull()
            .toMap()
        }

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
            .map { w ->
              async {
                w.id to wishlistsRemoteDataSource.fetchWishlistItemsCount(
                  w.id,
                  excludePurchased = true
                )
              }
            }
            .awaitAll()
            .toMap()
        }

        val sharedWishlistById = sharedWishlistsDeferred.await()
        val secretSantaEventsById = secretSantaEventsDeferred.await()
        val usersByUid = usersByUidDeferred.await()
        val categoriesById = categoriesByIdDeferred.await()
        val itemsCountById = itemsCountByIdDeferred.await()
        val itemsNonPurchasedCountById = itemsNonPurchasedCountByIdDeferred.await()

        wishlists
          .filter { w ->
            w.shareStatus == WishlistEntity.ShareStatus.Private || sharedWishlistById.containsKey(
              w.id
            )
          }
          .map { wishlist ->
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
              sharedWishlists = sharedWishlistById,
              secretSantaEvents = secretSantaEventsById,
              users = relatedUsers.map(userMapper::mapToBasic)
            )
          }
      }
    }

  /** Fetches one wishlist and resolves its related users, category and active links. */
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

        // SharedWishlists fetch
        val sharedWishlistDeferred = wishlist.sharedWishlistId?.let { shared ->
          async {
            val w = sharedWishlistsRemoteDataSource.fetchSharedWishlistById(shared)
            w?.let { wishlist.id to w }
          }
        }

        // SecretSantaEvents fetch
        val secretSantaEventsDeferred = async {
          val groups = groupsRemoteDataSource.fetchGroups(uid)
          secretSantaRemoteDataSource
            .fetchSecretSantaEvents(uid, groups.map { it.id })
            .filter { event -> event.deadline > nowInMillis() } // Non-expired
            .map { event ->
              async {
                val w = secretSantaRemoteDataSource.fetchParticipantWishlist(event.id, uid)
                w?.let { w.wishlist to event }
              }
            }
            .awaitAll()
            .filterNotNull()
            .toMap()
        }

        // Items count fetch
        val itemsCountByIdDeferred = async {
          wishlist.id to wishlistsRemoteDataSource.fetchWishlistItemsCount(wishlist.id)
        }

        // Non purchased items count fetch
        val itemsNonPurchasedCountByIdDeferred = async {
          wishlist.id to wishlistsRemoteDataSource.fetchWishlistItemsCount(
            wishlist.id,
            excludePurchased = true
          )
        }

        val sharedWishlist = sharedWishlistDeferred?.await()
        val secretSantaEventsById = secretSantaEventsDeferred.await()
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
          sharedWishlists = sharedWishlist?.let(::mapOf) ?: emptyMap(),
          secretSantaEvents = secretSantaEventsById,
          users = users.map(userMapper::mapToBasic)
        )
      }
    }

  /** Fetches and maps all items of a wishlist with their related user metadata. */
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

  /** Fetches and maps one wishlist item with its related user metadata. */
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

  /** Persists a newly created wishlist. */
  override suspend fun addWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: CreateWishlistRequest
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.wishlistFromRequest(uid, imageMedia, request)
      wishlistsRemoteDataSource.upsertWishlist(entity)
    }

  /** Persists the updated state of a wishlist. */
  override suspend fun updateWishlist(
    uid: String,
    imageMedia: ImageMedia,
    request: UpdateWishlistRequest
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.wishlistFromRequest(uid, imageMedia, request)
      wishlistsRemoteDataSource.upsertWishlist(entity)
    }

  /**
   * Creates the shared-wishlist header and marks the base wishlist as shared by
   * linking both persistence records.
   */
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

  /** Deletes a wishlist header. */
  override suspend fun deleteWishlist(wishlist: String): Result<Unit> =
    runCatching {
      wishlistsRemoteDataSource.removeWishlist(wishlistId = wishlist)
    }

  /** Persists a newly created wishlist item. */
  override suspend fun addWishlistItem(
    uid: String,
    imageMedia: ImageMedia?,
    request: CreateWishlistItemRequest
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.wishlistItemFromRequest(uid, imageMedia, request)
      wishlistsRemoteDataSource.upsertWishlistItem(wishlistId = request.wishlist, entity = entity)
    }

  /** Persists the updated state of a wishlist item. */
  override suspend fun updateWishlistItem(
    uid: String,
    imageMedia: ImageMedia?,
    request: UpdateWishlistItemRequest
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.wishlistItemFromRequest(uid, imageMedia, request)
      wishlistsRemoteDataSource.upsertWishlistItem(wishlistId = request.wishlist, entity = entity)
    }

  /** Persists the updated state of a personal category. */
  override suspend fun updateCategory(
    uid: String,
    category: Category
  ): Result<Unit> =
    runCatching {
      val entity = wishlistsMapper.mapCategory(category)
      wishlistsRemoteDataSource.upsertCategory(uid, entity)
    }

  /** Deletes a wishlist item. */
  override suspend fun deleteWishlistItem(
    wishlist: String,
    item: String
  ): Result<Unit> =
    runCatching {
      wishlistsRemoteDataSource.removeWishlistItem(wishlistId = wishlist, itemId = item)
    }

  /** Deletes a category and clears its usage from affected wishlists. */
  override suspend fun deleteCategory(
    uid: String,
    category: String
  ): Result<Unit> =
    runCatching {
      wishlistsRemoteDataSource.removeCategory(uid, category)
    }

  /** Extracts product metadata through the remote callable strategy. */
  override suspend fun extractUrlData(url: String): Result<WishlistItemUrlData> =
    runCatching {
      val result = wishlistsRemoteDataSource.extractUrlData(url)
      wishlistsMapper.mapUrlDataResult(result)
    }

  /** Completes URL metadata locally by loading the page when remote data is incomplete. */
  override suspend fun extractUrlDataLocally(
    data: WishlistItemUrlData,
    url: String
  ): Result<WishlistItemUrlData> =
    runCatching {
      val result = urlDataExtractor.extract(url)
      result?.let { wishlistsMapper.mergeUrlDataResults(data, result) } ?: data
    }

  /** Joins a wishlist as editor by using the invitation token. */
  override suspend fun addWishlistEditor(token: String): Result<Unit> =
    runCatching {
      wishlistsRemoteDataSource.joinToWishlistEditorsByToken(token)
    }
}
