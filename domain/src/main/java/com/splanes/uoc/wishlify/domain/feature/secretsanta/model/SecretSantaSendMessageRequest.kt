package com.splanes.uoc.wishlify.domain.feature.secretsanta.model

/**
 * Input required to send a Secret Santa chat message from the perspective of
 * the current user.
 */
sealed class SecretSantaSendMessageRequest(
  open val eventId: String,
  open val otherUid: String,
  open val text: String,
) {

  /** Message request where the current user is acting as the receiver. */
  data class AsReceiver(
    override val eventId: String,
    override val otherUid: String,
    override val text: String,
  ) : SecretSantaSendMessageRequest(
    eventId = eventId,
    otherUid = otherUid,
    text = text,
  )

  /** Message request where the current user is acting as the giver. */
  data class AsGiver(
    override val eventId: String,
    override val otherUid: String,
    override val text: String,
  ) : SecretSantaSendMessageRequest(
    eventId = eventId,
    otherUid = otherUid,
    text = text,
  )
}
