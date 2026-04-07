package com.splanes.uoc.wishlify.data.feature.groups.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.groups
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.feature.groups.model.GroupEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

class GroupsRemoteDataSource(
  private val db: FirebaseFirestore
) {

  private val groups by lazy { db.groups }

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

  suspend fun addGroup(entity: GroupEntity) {
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
}