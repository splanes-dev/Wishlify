package com.splanes.uoc.wishlify.data.feature.wishlists.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.users
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.wishlistCategories
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.wishlists
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.withBatch
import com.splanes.uoc.wishlify.data.feature.wishlists.model.CategoryEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

class WishlistsRemoteDataSource(
  private val db: FirebaseFirestore
) {

  private val wishlists by lazy { db.wishlists }

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

  suspend fun removeCategory(uid: String, category: CategoryEntity) {
    try {
      db.withBatch { batch ->
        // Delete category
        val ref = categoriesOf(uid).document(category.id)
        batch.delete(ref)

        // Delete the uses of the category
        val affected = wishlists
          .whereEqualTo("category.id", category.id)
          .whereEqualTo("category.owner", uid)
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
}