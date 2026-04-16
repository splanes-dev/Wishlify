package com.splanes.uoc.wishlify.domain.feature.secresanta.model

sealed class SecretSantaSendMessageRequest(
  open val eventId: String,
  open val otherUid: String,
  open val text: String,
) {

  data class AsReceiver(
    override val eventId: String,
    override val otherUid: String,
    override val text: String,
  ) : SecretSantaSendMessageRequest(
    eventId = eventId,
    otherUid = otherUid,
    text = text,
  )

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