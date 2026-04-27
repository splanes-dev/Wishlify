package com.splanes.uoc.wishlify.data.feature.user.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.systemUidByEmail
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.users
import com.splanes.uoc.wishlify.data.feature.user.model.UidByEmailEntity
import com.splanes.uoc.wishlify.data.feature.user.model.UserEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

/**
 * Firestore-backed data source for user persistence and lookup operations.
 *
 * It encapsulates profile storage plus the auxiliary email-hash index used for
 * user search, translating infrastructure failures into generic domain errors.
 */
class UserRemoteDataSource(
  private val db: FirebaseFirestore
) {

  private val users by lazy { db.users }
  private val uidByEmail by lazy { db.systemUidByEmail }

  /** Checks whether a user document exists for the given uid. */
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

  /** Retrieves a user profile by uid, or `null` when it does not exist. */
  suspend fun fetchUserById(uid: String): UserEntity? {
    try {
      val snapshot = users
        .document(uid)
        .get()
        .await()
      return snapshot.toObject<UserEntity>()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  /** Creates or replaces the persisted profile of a user. */
  suspend fun upsertUser(user: UserEntity) {
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

  /** Resolves a user uid from the hashed-email lookup index. */
  suspend fun searchUidByEmail(hash: String) =
    try {
      uidByEmail
        .document(hash)
        .get()
        .await()
        .toObject<UidByEmailEntity>()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  /** Searches users by their public user code. */
  suspend fun searchByCode(code: String) =
    try {
      users
        .whereEqualTo("code", code)
        .get()
        .await()
        .readAll<UserEntity>()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
}
