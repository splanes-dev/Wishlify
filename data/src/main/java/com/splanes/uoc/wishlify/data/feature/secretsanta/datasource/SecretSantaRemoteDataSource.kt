package com.splanes.uoc.wishlify.data.feature.secretsanta.datasource

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSanta
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSantaAssignments
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSantaParticipantsWishlist
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSantaParticipantsWishlistItems
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaAssignmentEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaEventEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaParticipantWishlistEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistItemEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

class SecretSantaRemoteDataSource(
  private val db: FirebaseFirestore
) {

  private val secretSanta by lazy { db.secretSanta }

  suspend fun fetchSecretSantaEvents(
    uid: String,
    groups: List<String>
  ): List<SecretSantaEventEntity> =
    try {
      val filters = buildList {
        add(Filter.equalTo("createdBy", uid))
        add(Filter.arrayContains("participants", uid))
        if (groups.isNotEmpty()) {
          add(Filter.inArray("group", groups))
        }
      }
      secretSanta
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

  suspend fun fetchSecretSantaEvent(eventId: String): SecretSantaEventEntity? =
    try {
      secretSanta
        .document(eventId)
        .get()
        .await()
        .toObject()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun upsertSecretSantaEvent(entity: SecretSantaEventEntity) {
    try {
      secretSanta
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

  suspend fun fetchAssignment(uid: String, eventId: String): SecretSantaAssignmentEntity? =
    try {
      assignmentOf(eventId, uid)
        .get()
        .await()
        .toObject()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun upsertAssignment(
    eventId: String,
    uid: String,
    assignment: SecretSantaAssignmentEntity
  ) {
    try {
      assignmentOf(eventId, uid)
        .set(assignment)
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun fetchParticipantWishlist(
    eventId: String,
    uid: String
  ): SecretSantaParticipantWishlistEntity? =
    try {
      participantsWishlistOf(eventId, uid)
        .get()
        .await()
        .toObject()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun upsertParticipantWishlist(
    eventId: String,
    uid: String,
    entity: SecretSantaParticipantWishlistEntity
  ) {
    try {
      participantsWishlistOf(eventId, uid)
        .set(entity)
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun upsertParticipantWishlistItem(
    eventId: String,
    uid: String,
    entity: WishlistItemEntity,
  ) {
    try {
      participantsWishlistItemOf(eventId, uid)
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

  private fun assignmentOf(id: String, uid: String) =
    secretSanta
      .document(id)
      .secretSantaAssignments
      .document(uid)

  private fun participantsWishlistOf(id: String, uid: String) =
    secretSanta
      .document(id)
      .secretSantaParticipantsWishlist
      .document(uid)

  private fun participantsWishlistItemOf(id: String, uid: String) =
    secretSanta
      .document(id)
      .secretSantaParticipantsWishlist
      .document(uid)
      .secretSantaParticipantsWishlistItems
}