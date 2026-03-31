package com.splanes.uoc.wishlify.data.feature.user.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.users
import com.splanes.uoc.wishlify.data.feature.user.model.UserDto
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

class UserRemoteDataSource(
  private val db: FirebaseFirestore
) {

  private val users by lazy { db.users }

  suspend fun existsById(uid: String): Boolean {
    try {
      val snapshot = users
        .document(uid)
        .get()
        .await()
      return snapshot.exists()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun add(user: UserDto) {
    try {
      users
        .document(user.uid)
        .set(user)
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }
}