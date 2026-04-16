package com.splanes.uoc.wishlify.data.feature.secretsanta.datasource

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.readAll
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSanta
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSantaAssignments
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSantaChatMessages
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSantaChats
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSantaParticipantsWishlist
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.secretSantaParticipantsWishlistItems
import com.splanes.uoc.wishlify.data.common.firebase.utils.db.withBatch
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaAssignmentEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaChatEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaChatMessageEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaEventEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaParticipantWishlistEntity
import com.splanes.uoc.wishlify.data.feature.wishlists.model.WishlistItemEntity
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.common.model.ChatPage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
      assignmentsOf(eventId)
        .document(uid)
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
      assignmentsOf(eventId)
        .document(uid)
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

  suspend fun removeParticipantWishlist(
    eventId: String,
    uid: String,
  ) {
    try {
      participantsWishlistOf(eventId, uid)
        .delete()
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun fetchParticipantWishlistItems(
    eventId: String,
    uid: String,
  ): List<WishlistItemEntity> =
    try {
      participantsWishlistItemOf(eventId, uid)
        .get()
        .await()
        .readAll()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
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

  suspend fun removeParticipantWishlistItems(
    eventId: String,
    uid: String,
  ) {
    try {

      val items = participantsWishlistItemOf(eventId, uid).get().await()

      db.withBatch { batch ->
        items
          .documents
          .forEach { doc -> batch.delete(doc.reference) }
      }
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  suspend fun upsertSecretSantaEventChat(
    eventId: String,
    entity: SecretSantaChatEntity
  ) {
    try {
      chatsOf(eventId)
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

  fun subscribeToChat(
    eventId: String,
    chatId: String,
    limit: Int
  ): Flow<List<SecretSantaChatMessageEntity>> =
    callbackFlow {
      val registration = chatMessagesOf(eventId, chatId)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(limit.toLong())
        .addSnapshotListener { snapshots, exception ->

          if (exception != null) {
            close(exception)
            return@addSnapshotListener
          }

          val messages = snapshots
            ?.readAll<SecretSantaChatMessageEntity>()
            .orEmpty()
            .reversed()

          trySend(messages)
        }

      awaitClose {
        registration.remove()
      }
    }

  suspend fun fetchSecretSantaEventChatMessages(
    eventId: String,
    chatId: String,
    from: Long,
    limit: Int
  ): ChatPage<SecretSantaChatMessageEntity> =
    try {
      val snapshot = chatMessagesOf(eventId, chatId)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .startAfter(from)
        .limit((limit + 1).toLong())
        .get()
        .await()

      val entities = snapshot.readAll<SecretSantaChatMessageEntity>()
      val hasMore = entities.size > limit
      val messages = entities.take(limit).reversed()
      val nextCursor = messages.firstOrNull()?.createdAt

      ChatPage(
        messages = messages,
        nextCursor = nextCursor,
        hasMore = hasMore
      )
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }

  suspend fun upsertSecretSantaEventChatMessage(
    eventId: String,
    chatId: String,
    entity: SecretSantaChatMessageEntity
  ) {
    try {
      chatMessagesOf(eventId, chatId)
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

  private fun assignmentsOf(id: String) =
    secretSanta
      .document(id)
      .secretSantaAssignments

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

  private fun chatsOf(id: String) =
    secretSanta
      .document(id)
      .secretSantaChats

  private fun chatMessagesOf(id: String, chatId: String) =
    chatsOf(id)
      .document(chatId)
      .secretSantaChatMessages
}