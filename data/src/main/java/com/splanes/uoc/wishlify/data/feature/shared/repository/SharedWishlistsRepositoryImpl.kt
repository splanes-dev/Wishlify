package com.splanes.uoc.wishlify.data.feature.shared.repository

import com.splanes.uoc.wishlify.data.feature.groups.datasource.GroupsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.groups.mapper.GroupsDataMapper
import com.splanes.uoc.wishlify.data.feature.shared.datasource.SharedWishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.shared.mapper.SharedWishlistsDataMapper
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.datasource.WishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.wishlists.mapper.WishlistsDataMapper
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SharedWishlistsRepositoryImpl(
  private val sharedWishlistsRemoteDataSource: SharedWishlistsRemoteDataSource,
  private val groupsRemoteDataSource: GroupsRemoteDataSource,
  private val wishlistsRemoteDataSource: WishlistsRemoteDataSource,
  private val userRemoteDataSource: UserRemoteDataSource,
  private val mapper: SharedWishlistsDataMapper,
  private val groupsMapper: GroupsDataMapper,
  private val wishlistsDataMapper: WishlistsDataMapper,
  private val userDataMapper: UserDataMapper,
) : SharedWishlistsRepository {

  override suspend fun fetchSharedWishlists(uid: String): Result<List<SharedWishlist>> =
    runCatching {
      val groups = groupsRemoteDataSource
        .fetchGroups(uid)
        .map { groupsMapper.mapToBasic(it, true) }
      val groupsId = groups.map { it.id }
      val entities = sharedWishlistsRemoteDataSource.fetchSharedWishlists(
        uid = uid,
        groups = groupsId
      )

      val groupsToFetch = entities
        .mapNotNull { entity -> entity.group }
        .filter { id -> id !in groupsId }
        .distinct()
      val wishlistsToFetch = entities
        .map { entity -> entity.wishlist }
      val usersToFetch = entities
        .flatMap { entity -> entity.participants + entity.owner + entity.editors }
        .distinct()

      coroutineScope {
        val groupsByIdDeferred = async {
          groupsToFetch
            .map { id -> async { groupsRemoteDataSource.fetchGroupById(id) } }
            .awaitAll()
            .filterNotNull()
            .map { groupsMapper.mapToBasic(it, true) }
            .associateBy { it.id }
        }
        val wishlistsByIdDeferred = async {
          wishlistsToFetch
            .map { id -> async { wishlistsRemoteDataSource.fetchWishlist(id) } }
            .awaitAll()
            .map { wishlist -> wishlistsDataMapper.mapToLinkedWishlist(wishlist) }
            .associateBy { it.id }
        }
        val usersByIdDeferred = async {
          usersToFetch
            .map { id -> async { userRemoteDataSource.fetchUserById(id) } }
            .awaitAll()
            .filterNotNull()
            .map { user -> userDataMapper.mapToBasic(user) }
            .map { basic -> userDataMapper.map(basic) }
            .associateBy { it.uid }
        }
        val numOfItemsDeferred = async {
          entities
            .map { entity ->
              async { entity.id to wishlistsRemoteDataSource.fetchWishlistItemsCount(entity.wishlist) }
            }
            .awaitAll()
            .toMap()
        }

        val groupsById = groupsByIdDeferred.await() + groups.associateBy { it.id }
        val wishlistsById = wishlistsByIdDeferred.await()
        val usersById = usersByIdDeferred.await()
        val numOfItemsById = numOfItemsDeferred.await()

        entities.map { entity ->
          mapper.mapWishlist(
            uid = uid,
            entity = entity,
            groups = groupsById,
            wishlists = wishlistsById,
            users = usersById,
            numOfItemsMap = numOfItemsById,
            pendingNotificationsMap = emptyMap() // TODO
          )
        }
      }
    }

  override suspend fun fetchSharedWishlist(
    uid: String,
    sharedWishlistId: String
  ): Result<SharedWishlist> =
    runCatching {
      val entity = sharedWishlistsRemoteDataSource.fetchSharedWishlistById(sharedWishlistId)
        ?: throw GenericError.Unknown()

      val groupToFetch = entity.group
      val wishlistToFetch = entity.wishlist
      val usersToFetch = buildList {
        addAll(entity.participants)
        add(entity.owner)
        addAll(entity.editors)
      }.distinct()

      coroutineScope {
        val groupsByIdDeferred = async {
          groupToFetch
            ?.let { id -> groupsRemoteDataSource.fetchGroupById(id) }
            ?.let { entity -> groupsMapper.mapToBasic(entity, true) }
            ?.let { group -> mapOf(group.id to group) } ?: emptyMap()
        }

        val wishlistsByIdDeferred = async {
          wishlistToFetch
            .let { id -> wishlistsRemoteDataSource.fetchWishlist(id) }
            .let(wishlistsDataMapper::mapToLinkedWishlist)
            .let { wishlist -> mapOf(wishlist.id to wishlist) }
        }

        val usersByIdDeferred = async {
          usersToFetch
            .map { id -> async { userRemoteDataSource.fetchUserById(id) } }
            .awaitAll()
            .filterNotNull()
            .map { user -> userDataMapper.mapToBasic(user) }
            .map { basic -> userDataMapper.map(basic) }
            .associateBy { it.uid }
        }

        val numOfItemsDeferred = async {
          val count = wishlistsRemoteDataSource.fetchWishlistItemsCount(entity.wishlist)
          mapOf(entity.id to count)
        }

        val groupsById = groupsByIdDeferred.await()
        val wishlistsById = wishlistsByIdDeferred.await()
        val usersById = usersByIdDeferred.await()
        val numOfItemsById = numOfItemsDeferred.await()

        mapper.mapWishlist(
          uid = uid,
          entity = entity,
          groups = groupsById,
          wishlists = wishlistsById,
          users = usersById,
          numOfItemsMap = numOfItemsById,
          pendingNotificationsMap = emptyMap() // TODO
        )
      }
    }

  override suspend fun fetchSharedWishlistItems(
    uid: String,
    sharedWishlistId: String
  ): Result<List<SharedWishlistItem>> =
    runCatching {
      coroutineScope {
        val sharedWishlist =
          sharedWishlistsRemoteDataSource.fetchSharedWishlistById(sharedWishlistId)
            ?: throw GenericError.Unknown()

        val baseItemsDeferred = async {
          wishlistsRemoteDataSource.fetchWishlistItems(sharedWishlist.wishlist)
        }

        val sharedItemsDeferred = async {
          sharedWishlistsRemoteDataSource.fetchSharedWishlistItems(sharedWishlistId)
        }

        val baseItems = baseItemsDeferred.await()
        val sharedItems = sharedItemsDeferred.await()

        val usersToFetch = sharedItems.flatMap { entity ->
          buildList {
            entity.reservation?.reservedBy?.let(::add)
            entity.reservation?.reservedByGroup?.let(::addAll)
            entity.purchased?.purchasedBy?.let(::add)
            entity.purchased?.purchasedByGroup?.let(::addAll)
            entity.shareRequest?.requestedBy?.let(::add)
            entity.shareRequest?.participantsJoined?.let(::addAll)
          }
        }.distinct()

        val usersById = usersToFetch
          .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
          .awaitAll()
          .filterNotNull()
          .map { entity -> userDataMapper.mapToBasic(entity) }
          .map { basic -> userDataMapper.map(basic) }
          .associateBy { user -> user.uid }

        val sharedItemById = sharedItems.associateBy { it.item }

        baseItems
          .map { entity -> wishlistsDataMapper.mapToLinkedItem(entity) }
          .map { linkedItem ->
            mapper.mapItem(
              uid = uid,
              linkedItem = linkedItem,
              sharedItem = sharedItemById[linkedItem.id],
              users = usersById
            )
          }
      }
    }

  override suspend fun fetchSharedWishlistItem(
    uid: String,
    sharedWishlistId: String,
    sharedWishlistItemId: String
  ): Result<SharedWishlistItem> =
    runCatching {
      coroutineScope {
        val sharedWishlistDeferred = async {
          sharedWishlistsRemoteDataSource.fetchSharedWishlistById(sharedWishlistId)
        }

        val sharedWishlistItemDeferred = async {
          sharedWishlistsRemoteDataSource.fetchSharedWishlistItemById(
            wishlist = sharedWishlistId,
            item = sharedWishlistItemId
          )
        }

        val sharedWishlist = sharedWishlistDeferred.await() ?: throw GenericError.Unknown()
        val sharedWishlistItem = sharedWishlistItemDeferred.await() ?: throw GenericError.Unknown()

        val baseItem = wishlistsRemoteDataSource.fetchWishlistItem(
          wishlistId = sharedWishlist.wishlist,
          itemId = sharedWishlistItem.item
        )

        val usersToFetch = buildList {
          sharedWishlistItem.reservation?.reservedBy?.let(::add)
          sharedWishlistItem.reservation?.reservedByGroup?.let(::addAll)
          sharedWishlistItem.purchased?.purchasedBy?.let(::add)
          sharedWishlistItem.purchased?.purchasedByGroup?.let(::addAll)
          sharedWishlistItem.shareRequest?.requestedBy?.let(::add)
          sharedWishlistItem.shareRequest?.participantsJoined?.let(::addAll)
        }.distinct()

        val usersById = usersToFetch
          .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
          .awaitAll()
          .filterNotNull()
          .map { entity -> userDataMapper.mapToBasic(entity) }
          .map { basic -> userDataMapper.map(basic) }
          .associateBy { user -> user.uid }

        mapper.mapItem(
          uid = uid,
          linkedItem = wishlistsDataMapper.mapToLinkedItem(baseItem),
          sharedItem = sharedWishlistItem,
          users = usersById
        )
      }
    }

  override suspend fun updateSharedWishlistItemState(
    uid: String,
    request: SharedWishlistItemUpdateStateRequest
  ): Result<Unit> =
    runCatching {
      val entity = mapper.sharedItemEntityFromRequest(uid, request)
      sharedWishlistsRemoteDataSource.upsertSharedWishlistItem(
        wishlist = request.sharedWishlist.id,
        entity = entity
      )
    }
}