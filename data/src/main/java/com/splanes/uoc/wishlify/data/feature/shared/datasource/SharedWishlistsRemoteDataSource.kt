package com.splanes.uoc.wishlify.data.feature.shared.datasource

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.chatMessages
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.sharedWishlistItems
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.sharedWishlists
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.withBatch
import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistChatMessageEntity
import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistEntity
import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistItemEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.common.model.ChatPage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

class SharedWishlistsRemoteDataSource(
  private val db: FirebaseFirestore
) {

  private val sharedWishlists by lazy { db.sharedWishlists }

  suspend fun fetchSharedWishlists(uid: String, groups: List<String>): List<SharedWishlistEntity> =
    try {
      val filters = buildList {
        add(Filter.arrayContains("editors", uid))
        add(Filter.arrayContains("participants", uid))
        if (groups.isNotEmpty()) {
          add(Filter.inArray("group", groups))
        }
      }
      sharedWishlists
        .where(Filter.or(*filters.toTypedArray()))
        .get()
        .await()
        .readAll()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun fetchSharedWishlistById(id: String): SharedWishlistEntity? =
    try {
      sharedWishlists
        .document(id)
        .get()
        .await()
        .toObject()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun upsertSharedWishlist(entity: SharedWishlistEntity) {
    try {
      sharedWishlists
        .document(entity.id)
        .set(entity)
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun countSharedWishlistsByGroup(groupId: String) =
    try {
      sharedWishlists
        .whereEqualTo("group", groupId)
        .get()
        .await()
        .count()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun removeWishlist(id: String, items: List<String>) {
    try {
      val chat = wishlistChatOf(id)
        .get()
        .await()
        .documents

      db.withBatch { batch ->

        chat.forEach { batch.delete(it.reference) }

        items.forEach { item ->
          val ref = wishlistItemsOf(id).document(item)
          batch.delete(ref)
        }
        val ref = sharedWishlists.document(id)
        batch.delete(ref)
      }
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun fetchSharedWishlistItems(wishlist: String): List<SharedWishlistItemEntity> =
    try {
      wishlistItemsOf(wishlist)
        .get()
        .await()
        .readAll()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun fetchSharedWishlistItemById(
    wishlist: String,
    item: String
  ): SharedWishlistItemEntity? =
    try {
      wishlistItemsOf(wishlist)
        .document(item)
        .get()
        .await()
        .toObject()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun upsertSharedWishlistItem(wishlist: String, entity: SharedWishlistItemEntity) {
    try {
      wishlistItemsOf(wishlist)
        .document(entity.id)
        .set(entity)
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun fetchSharedWishlistChatMessages(
    wishlist: String,
    from: Long,
    limit: Int
  ) =
    try {
      val snapshot = wishlistChatOf(wishlist)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .startAfter(from)
        .limit((limit + 1).toLong())
        .get()
        .await()

      val entities = snapshot.readAll<SharedWishlistChatMessageEntity>()
      val hasMore = entities.size > limit
      val messages = entities.take(limit).reversed()
      val nextCursor = messages.firstOrNull()?.createdAt

      ChatPage(
        messages = messages,
        nextCursor = nextCursor,
        hasMore = hasMore
      )
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun upsertSharedWishlistMessage(
    wishlist: String,
    entity: SharedWishlistChatMessageEntity
  ) {
    try {
      wishlistChatOf(wishlist)
        .document(entity.id)
        .set(entity)
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  fun subscribeToChat(wishlist: String, limit: Int): Flow<List<SharedWishlistChatMessageEntity>> =
    callbackFlow {

      val registration = wishlistChatOf(wishlist)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(limit.toLong())
        .addSnapshotListener { snapshots, exception ->

          if (exception != null) {
            close(exception)
            return@addSnapshotListener
          }

          val messages = snapshots
            ?.readAll<SharedWishlistChatMessageEntity>()
            .orEmpty()
            .reversed()

          trySend(messages)
        }

      awaitClose {
        registration.remove()
      }
    }.distinctUntilChanged()

  private fun wishlistItemsOf(id: String) =
    db.sharedWishlists.document(id).sharedWishlistItems

  private fun wishlistChatOf(id: String) =
    db.sharedWishlists.document(id).chatMessages
}