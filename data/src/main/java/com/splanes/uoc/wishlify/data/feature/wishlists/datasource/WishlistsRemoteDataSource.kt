package com.splanes.uoc.wishlify.data.feature.wishlists.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.users
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.wishlistCategories
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.wishlistItems
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.wishlists
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.withBatch
import com.splanes.uoc.wishlify.data.feature.wishlists.model.CategoryEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistItemEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

class WishlistsRemoteDataSource(
  private val db: FirebaseFirestore
) {

  private val wishlists by lazy { db.wishlists }

  suspend fun fetchWishlists(uid: String): List<WishlistEntity> =
    try {
      wishlists
        .whereArrayContains("editors", uid)
        .get()
        .await()
        .readAll()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun fetchWishlist(id: String): WishlistEntity =
    try {
      wishlists
        .document(id)
        .get()
        .await()
        .toObject<WishlistEntity>() ?: throw GenericError.Unknown()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun fetchWishlistItemsCount(id: String, excludePurchased: Boolean = false): Int =
    try {
      wishlistItemsOf(id)
        .let { query ->
          if (excludePurchased) {
            query.whereEqualTo("purchased", null)
          } else {
            query
          }
        }
        .get()
        .await()
        .count()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun fetchWishlistItems(id: String): List<WishlistItemEntity> =
    try {
      wishlistItemsOf(id)
        .get()
        .await()
        .readAll()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun fetchWishlistItem(wishlistId: String, itemId: String): WishlistItemEntity =
    try {
      wishlistItemsOf(wishlistId)
        .document(itemId)
        .get()
        .await()
        .toObject() ?: throw GenericError.Unknown()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun upsertWishlist(entity: WishlistEntity) {
    try {
      wishlists
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

  suspend fun removeWishlist(wishlistId: String) {
    try {
      wishlists
        .document(wishlistId)
        .delete()
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun upsertWishlistItem(wishlistId: String, entity: WishlistItemEntity) {
    try {
      wishlistItemsOf(wishlistId)
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

  suspend fun removeWishlistItem(wishlistId: String, itemId: String) {
    try {
      wishlistItemsOf(wishlistId)
        .document(itemId)
        .delete()
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun removeWishlistItems(wishlistId: String, items: List<String>) {
    try {
      db.withBatch { batch ->
        items.forEach { item ->
          val ref = wishlistItemsOf(wishlistId).document(item)
          batch.delete(ref)
        }
      }
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun fetchCategories(uid: String) =
    try {
      categoriesOf(uid)
        .get()
        .await()
        .readAll<CategoryEntity>()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun fetchCategoryById(uid: String, id: String) =
    try {
      categoriesOf(uid)
        .document(id)
        .get()
        .await()
        .toObject<CategoryEntity>()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun upsertCategory(uid: String, category: CategoryEntity) {
    try {
      categoriesOf(uid)
        .document(category.id)
        .set(category)
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun removeCategory(uid: String, category: String) {
    try {
      db.withBatch { batch ->
        // Delete category
        val ref = categoriesOf(uid).document(category)
        batch.delete(ref)

        // Delete the uses of the category
        val affected = wishlists
          .whereEqualTo("category.id", category)
          .whereEqualTo("category.owner", uid)
          .whereArrayContains("editors", uid)
          .get()
          .await()
          .documents

        affected.forEach { doc ->
          batch.update(doc.reference, "category", null)
        }
      }
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  private fun categoriesOf(uid: String) =
    db.users.document(uid).wishlistCategories

  private fun wishlistItemsOf(id: String) =
    db.wishlists.document(id).wishlistItems
}