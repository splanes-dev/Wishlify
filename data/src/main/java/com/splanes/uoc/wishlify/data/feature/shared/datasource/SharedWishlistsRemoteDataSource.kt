package com.splanes.uoc.wishlify.data.feature.shared.datasource

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.sharedWishlistItems
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.sharedWishlists
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.withBatch
import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistEntity
import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistItemEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
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
      db.withBatch { batch ->
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

  private fun wishlistItemsOf(id: String) =
    db.sharedWishlists.document(id).sharedWishlistItems
}