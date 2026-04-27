package com.splanes.uoc.wishlify.data.feature.groups.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.groups
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.feature.groups.model.GroupEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

/**
 * Firestore-backed data source for group persistence and retrieval.
 *
 * It translates Firestore and connectivity failures into domain-facing generic errors.
 */
class GroupsRemoteDataSource(
  private val db: FirebaseFirestore
) {

  private val groups by lazy { db.groups }

  /** Retrieves all groups where the given [uid] is a member. */
  suspend fun fetchGroups(uid: String): List<GroupEntity> =
    try {
      groups
        .whereArrayContains("members", uid)
        .get()
        .await()
        .readAll()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  /** Retrieves a single group by its identifier, or `null` when it does not exist. */
  suspend fun fetchGroupById(id: String): GroupEntity? =
    try {
      groups
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

  /** Creates or replaces the persisted state of a group entity. */
  suspend fun upsertGroup(entity: GroupEntity) {
    try {
      groups
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

  /** Deletes the group identified by [id] from Firestore. */
  suspend fun deleteGroup(id: String) {
    try {
      groups
        .document(id)
        .delete()
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }
}
