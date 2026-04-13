package com.splanes.uoc.wishlify.data.feature.shared.repository

import com.splanes.uoc.wishlify.data.common.media.datasource.MediaRemoteDataSource
import com.splanes.uoc.wishlify.data.common.media.mapper.ImageMediaDataMapper
import com.splanes.uoc.wishlify.data.feature.groups.datasource.GroupsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.groups.mapper.GroupsDataMapper
import com.splanes.uoc.wishlify.data.feature.shared.datasource.SharedWishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.shared.mapper.SharedWishlistsDataMapper
import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistChatMessageEntity
import com.splanes.uoc.wishlify.data.feature.user.datasource.UserRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.user.mapper.UserDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.datasource.WishlistsRemoteDataSource
import com.splanes.uoc.wishlify.data.feature.wishlists.mapper.WishlistsDataMapper
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.model.ChatPage
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlist
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistChatMessage
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItem
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistItemUpdateStateRequest
import com.splanes.uoc.wishlify.domain.feature.shared.model.SharedWishlistSendMessageRequest
import com.splanes.uoc.wishlify.domain.feature.shared.repository.SharedWishlistsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SharedWishlistsRepositoryImpl(
  private val sharedWishlistsRemoteDataSource: SharedWishlistsRemoteDataSource,
  private val groupsRemoteDataSource: GroupsRemoteDataSource,
  private val wishlistsRemoteDataSource: WishlistsRemoteDataSource,
  private val userRemoteDataSource: UserRemoteDataSource,
  private val mediaRemoteDataSource: MediaRemoteDataSource,
  private val mapper: SharedWishlistsDataMapper,
  private val groupsMapper: GroupsDataMapper,
  private val wishlistsDataMapper: WishlistsDataMapper,
  private val userDataMapper: UserDataMapper,
  private val mediaDataMapper: ImageMediaDataMapper
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
              async {
                entity.id to wishlistsRemoteDataSource.fetchWishlistItemsCount(
                  entity.wishlist,
                  excludePurchased = true
                )
              }
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
          val count = wishlistsRemoteDataSource.fetchWishlistItemsCount(
            entity.wishlist,
            excludePurchased = true
          )
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

  override suspend fun unshareSharedWishlist(
    sharedWishlistId: String,
    linkedWishlistId: String
  ): Result<Unit> =
    runCatching {
      coroutineScope {
        val sharedItemsDeferred = async {
          sharedWishlistsRemoteDataSource.fetchSharedWishlistItems(sharedWishlistId)
        }

        val baseWishlistDeferred = async {
          wishlistsRemoteDataSource.fetchWishlist(linkedWishlistId)
        }

        val sharedItems = sharedItemsDeferred.await()
        val baseWishlist = baseWishlistDeferred.await()

        val sharedItemsToDelete = sharedItems.map { it.id }
        val baseItemsToDelete = sharedItems
          .filter { it.purchased != null }
          .map { it.item }

        val updatedWishlist = baseWishlist.copy(
          shareStatus = WishlistEntity.ShareStatus.Private,
          sharedWishlistId = null
        )

        // Update linked wishlist
        wishlistsRemoteDataSource.upsertWishlist(updatedWishlist)

        // Batch delete of purchased items & shared-wishlist (+ items + chat)
        wishlistsRemoteDataSource.removeWishlistItems(linkedWishlistId, baseItemsToDelete)
        sharedWishlistsRemoteDataSource.removeWishlist(sharedWishlistId, sharedItemsToDelete)

        launch {
          baseItemsToDelete
            .map { item -> ImageMediaPath.WishlistItem(linkedWishlistId, item) }
            .map { path -> mediaDataMapper.pathOf(path) }
            .forEach { path -> mediaRemoteDataSource.delete(path) }
        }
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
          .filter { entity -> entity.purchased == null }
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

  override fun subscribeToWishlistsChatMessages(
    uid: String,
    sharedWishlistId: String,
    limit: Int
  ): Flow<List<SharedWishlistChatMessage>> =
    sharedWishlistsRemoteDataSource
      .subscribeToChat(sharedWishlistId, limit)
      .map { entities ->
        coroutineScope {
          val users = entities
            .asSequence()
            .filter { msg -> msg.type == SharedWishlistChatMessageEntity.Type.User }
            .map { msg -> msg.createdBy }
            .distinct()
            .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
            .toList()
            .awaitAll()
            .asSequence()
            .filterNotNull()
            .map { user -> userDataMapper.mapToBasic(user) }
            .map { basic -> userDataMapper.map(basic) }
            .associateBy { user -> user.uid }

          entities.map { entity -> mapper.mapMessage(uid, entity, users) }
        }
      }

  override suspend fun fetchSharedWishlistMessages(
    uid: String,
    wishlistId: String,
    cursor: Long,
    limit: Int
  ): Result<ChatPage<SharedWishlistChatMessage>> =
    runCatching {
      val page = sharedWishlistsRemoteDataSource.fetchSharedWishlistChatMessages(
        wishlist = wishlistId,
        from = cursor,
        limit = limit
      )

      val users = coroutineScope {
        page
          .messages
          .filter { msg -> msg.type == SharedWishlistChatMessageEntity.Type.User }
          .map { msg -> msg.createdBy }
          .distinct()
          .map { uid -> async { userRemoteDataSource.fetchUserById(uid) } }
          .awaitAll()
          .filterNotNull()
          .map { user -> userDataMapper.mapToBasic(user) }
          .map { basic -> userDataMapper.map(basic) }
          .associateBy { user -> user.uid }
      }

      val messages = page.messages.map { entity -> mapper.mapMessage(uid, entity, users) }

      ChatPage(
        messages = messages,
        nextCursor = page.nextCursor,
        hasMore = page.hasMore
      )
    }

  override suspend fun sendMessageToChat(
    uid: String,
    request: SharedWishlistSendMessageRequest
  ): Result<Unit> =
    runCatching {
      val entity = mapper.mapMessage(uid, request)
      sharedWishlistsRemoteDataSource
        .upsertSharedWishlistMessage(request.wishlist, entity)
    }
}