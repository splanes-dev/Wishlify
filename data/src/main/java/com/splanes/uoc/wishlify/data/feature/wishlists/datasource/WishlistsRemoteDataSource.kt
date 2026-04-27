package com.splanes.uoc.wishlify.data.feature.wishlists.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.functions.FirebaseFunctions
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.users
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.wishlistCategories
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.wishlistItems
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.wishlists
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.withBatch
import com.splanes.uoc.wishlify.data.common.firebase.utils.functions.JoinByInvitationLinkType
import com.splanes.uoc.wishlify.data.common.firebase.utils.functions.extractLinkMetadata
import com.splanes.uoc.wishlify.data.common.firebase.utils.functions.joinByInvitationLink
import com.splanes.uoc.wishlify.data.feature.wishlists.model.CategoryEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistItemEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

/**
 * Firestore and callable-functions backed data source for wishlists.
 *
 * It encapsulates wishlist headers, items, personal categories and invitation
 * or metadata extraction callables, translating infrastructure failures into
 * domain-facing generic errors.
 */
class WishlistsRemoteDataSource(
  private val db: FirebaseFirestore,
  private val functions: FirebaseFunctions
) {

  private val wishlists by lazy { db.wishlists }

  /** Retrieves the wishlists where the user currently acts as editor. */
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

  /** Retrieves one wishlist by id or throws when it cannot be resolved. */
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

  /** Counts the items of a wishlist, optionally excluding purchased ones. */
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

  /** Retrieves all persisted items belonging to a wishlist. */
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

  /** Retrieves a single wishlist item or throws when it does not exist. */
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

  /** Creates or replaces a wishlist document. */
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

  /** Deletes a wishlist document. */
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

  /** Creates or replaces a wishlist item document. */
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

  /** Deletes a single wishlist item. */
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

  /** Deletes several wishlist items in a single batch operation. */
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

  /** Retrieves the personal categories owned by the given user. */
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

  /** Retrieves one personal category by id, if it exists. */
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

  /** Creates or replaces one personal category document. */
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

  /**
   * Deletes a category and clears its references from the user-owned wishlists
   * that currently use it.
   */
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

  /** Invokes the server-side metadata extractor for a product URL. */
  suspend fun extractUrlData(link: String): Map<*, *>? {
    try {
      val result = functions
        .extractLinkMetadata(link)
        .await()

      return result.data as? Map<*, *>

    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  /** Joins a wishlist as editor through the shared invitation-link callable. */
  suspend fun joinToWishlistEditorsByToken(token: String) =
    try {
      functions
        .joinByInvitationLink(token, type = JoinByInvitationLinkType.WishlistEditor)
        .await()

    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  /** Returns the categories subcollection owned by the given user. */
  private fun categoriesOf(uid: String) =
    db.users.document(uid).wishlistCategories

  /** Returns the items subcollection for the given wishlist. */
  private fun wishlistItemsOf(id: String) =
    db.wishlists.document(id).wishlistItems
}
