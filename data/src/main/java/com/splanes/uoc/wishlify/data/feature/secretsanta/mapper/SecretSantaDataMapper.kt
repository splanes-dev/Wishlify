package com.splanes.uoc.wishlify.data.feature.secretsanta.mapper

import com.splanes.uoc.wishlify.data.common.utils.nowInMillis
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaChatEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaChatMessageEntity
import com.splanes.uoc.wishlify.data.feature.secretsanta.model.SecretSantaEventEntity
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.common.model.InviteLink
import com.splanes.uoc.wishlify.domain.common.utils.newUuid
import com.splanes.uoc.wishlify.domain.feature.groups.model.Group
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.CreateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaChatMessage
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEvent
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.SecretSantaEventDetail
import com.splanes.uoc.wishlify.domain.feature.secretsanta.model.UpdateSecretSantaEventRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import java.util.Date

/** Maps Secret Santa entities between Firestore persistence and domain models. */
class SecretSantaDataMapper {

  /** Maps a stored event into its lightweight domain projection. */
  fun map(
    entity: SecretSantaEventEntity,
    assignments: Map<String, User.Basic>
  ): SecretSantaEvent =
    when (entity.drawStatus) {
      SecretSantaEventEntity.DrawStatus.Pending ->
        SecretSantaEvent.DrawPending(
          id = entity.id,
          name = entity.name,
          photoUrl = entity.photoUrl?.takeIf { it.isBlank() },
          group = entity.group,
          deadline = Date(entity.deadline)
        )

      SecretSantaEventEntity.DrawStatus.Done ->
        SecretSantaEvent.DrawDone(
          id = entity.id,
          name = entity.name,
          photoUrl = entity.photoUrl?.takeIf { it.isBlank() },
          group = entity.group,
          deadline = Date(entity.deadline),
          target = assignments[entity.id]?.username
            ?: error("No assignment found for event `${entity.id}`. Draw is supposed to be done")
        )
    }

  /** Maps an event creation request into its Firestore persistence model. */
  fun map(
    uid: String,
    media: ImageMedia?,
    request: CreateSecretSantaEventRequest
  ): SecretSantaEventEntity =
    SecretSantaEventEntity(
      id = request.id,
      name = request.name,
      photoUrl = when (media) {
        is ImageMedia.Preset -> null
        is ImageMedia.Url -> media.url
        null -> null
      },
      budget = request.budget,
      budgetApproximate = request.isBudgetApproximate,
      deadline = request.deadline.time,
      createdBy = uid,
      createdAt = nowInMillis(),
      group = request.group?.id,
      participants = request.participants.map { participant -> participant.uid },
      inviteLink = request.inviteLink.token,
      exclusions = request.exclusions
        .map { (m1, m2) -> m1.uid to m2.uid }
        .groupBy(
          keySelector = { (m1, _) -> m1 },
          valueTransform = { (_, m2) -> m2 }
        ),
      drawStatus = SecretSantaEventEntity.DrawStatus.Pending,
    )

  /** Maps an event update request into its Firestore persistence model. */
  fun map(
    uid: String,
    media: ImageMedia?,
    request: UpdateSecretSantaEventRequest
  ): SecretSantaEventEntity =
    SecretSantaEventEntity(
      id = request.id,
      name = request.name,
      photoUrl = when (media) {
        is ImageMedia.Preset -> null
        is ImageMedia.Url -> media.url
        null -> null
      },
      budget = request.budget,
      budgetApproximate = request.isBudgetApproximate,
      deadline = request.deadline.time,
      createdBy = uid,
      createdAt = nowInMillis(),
      group = request.group?.id,
      participants = request.participants.map { participant -> participant.uid },
      inviteLink = request.inviteLink.token,
      exclusions = request.exclusions
        .map { (m1, m2) -> m1.uid to m2.uid }
        .groupBy(
          keySelector = { (m1, _) -> m1 },
          valueTransform = { (_, m2) -> m2 }
        ),
      drawStatus = SecretSantaEventEntity.DrawStatus.Pending,
    )

  /**
   * Maps a stored event into its detailed domain projection using resolved
   * users, group metadata and assignment context.
   */
  fun mapDetail(
    entity: SecretSantaEventEntity,
    group: Group.Basic?,
    receiver: String?,
    giver: String?,
    receiverWishlist: String?,
    receiverSharedHobbies: Boolean,
    currentUserWishlist: String?,
    users: Map<String, User.Basic>
  ): SecretSantaEventDetail =
    when (entity.drawStatus) {
      SecretSantaEventEntity.DrawStatus.Pending ->
        SecretSantaEventDetail.DrawPending(
          id = entity.id,
          photoUrl = entity.photoUrl,
          name = entity.name,
          budget = entity.budget,
          isBudgetApproximate = entity.budgetApproximate,
          group = group,
          participants = entity.participants.map { participant ->
            users[participant] ?: error("No user found for participant id $participant")
          },
          exclusions = entity.exclusions
            .mapKeys { (uid, _) -> users[uid] ?: error("No user found for exclusion user id $uid") }
            .mapValues { (_, uids) -> uids.mapNotNull { users[it] } },
          deadline = Date(entity.deadline),
          inviteLink = InviteLink(
            token = entity.inviteLink,
            origin = InviteLink.SecretSanta
          ),
          createdBy = users[entity.createdBy] ?: error("No user found for createdBy user id ${entity.createdBy}"),
          createdAt = Date(entity.createdAt)
        )

      SecretSantaEventEntity.DrawStatus.Done ->
        SecretSantaEventDetail.DrawDone(
          id = entity.id,
          photoUrl = entity.photoUrl,
          name = entity.name,
          budget = entity.budget,
          isBudgetApproximate = entity.budgetApproximate,
          group = group,
          participants = entity.participants.map { participant ->
            users[participant] ?: error("No user found for participant id $participant")
          },
          exclusions = entity.exclusions
            .mapKeys { (uid, _) -> users[uid] ?: error("No user found for exclusion user id $uid") }
            .mapValues { (_, uids) -> uids.mapNotNull { users[it] } },
          deadline = Date(entity.deadline),
          inviteLink = InviteLink(
            token = entity.inviteLink,
            origin = InviteLink.SecretSanta
          ),
          receiver = users[receiver ?: error("Draw done but no receiver uid")] ?: error("No user found for receiver id $receiver"),
          receiverSharedWishlist = receiverWishlist,
          currentUserSharedWishlist = currentUserWishlist,
          receiverSharedHobbies = receiverSharedHobbies,
          receiverChatNotificationCount = 0, // TODO
          giver = giver ?: error("No giver found"),
          giverChatNotificationCount = 0, // TODO
          createdBy = users[entity.createdBy] ?: error("No user found for createdBy user id ${entity.createdBy}"),
          createdAt = Date(entity.createdAt)
        )
    }

  /** Builds the chat metadata that becomes available after a successful draw. */
  fun createChatsFromAssignments(assignments: Map<String, String>): List<SecretSantaChatEntity> =
    assignments.map { (giver, receiver) ->
      SecretSantaChatEntity(
        id = "${giver}_${receiver}",
        receiver = receiver,
        giver = giver,
        createdAt = nowInMillis()
      )
    }

  /** Maps a persisted chat message into its domain representation. */
  fun mapMessage(
    entity: SecretSantaChatMessageEntity,
    chatId: String,
    uid: String,
    users: List<User.Basic>
  ): SecretSantaChatMessage =
    SecretSantaChatMessage(
      chatId = chatId,
      messageId = entity.id,
      sender = users.first { it.uid == entity.sender },
      text = entity.text,
      sentAt = Date(entity.createdAt),
      isCurrentUserMessage = entity.sender == uid
    )

  /** Builds the persistence model for a newly sent chat message. */
  fun mapMessage(uid: String, text: String): SecretSantaChatMessageEntity =
    SecretSantaChatMessageEntity(
      id = newUuid(),
      sender = uid,
      text = text,
      createdAt = nowInMillis()
    )
}
