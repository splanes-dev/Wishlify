package com.splanes.uoc.wishlify.domain.feature.secretsanta.model

/**
 * Input required to resolve a Secret Santa chat from the perspective of the
 * current user.
 */
sealed class GetSecretSantaChatRequest(
  open val eventId: String,
  open val otherUid: String,
) {

  /** Chat request where the current user is acting as the receiver. */
  data class AsReceiver(
    override val eventId: String,
    override val otherUid: String
  ) : GetSecretSantaChatRequest(
    eventId = eventId,
    otherUid = otherUid
  )

  /** Chat request where the current user is acting as the giver. */
  data class AsGiver(
    override val eventId: String,
    override val otherUid: String
  ) : GetSecretSantaChatRequest(
    eventId = eventId,
    otherUid = otherUid
  )
}
