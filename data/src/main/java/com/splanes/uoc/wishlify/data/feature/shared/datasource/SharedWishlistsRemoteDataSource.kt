package com.splanes.uoc.wishlify.data.feature.shared.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.sharedWishlists
import com.splanes.uoc.wishlify.data.feature.shared.model.SharedWishlistEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

class SharedWishlistsRemoteDataSource(
  private val db: FirebaseFirestore
) {

  private val sharedWishlists by lazy { db.sharedWishlists }

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
}