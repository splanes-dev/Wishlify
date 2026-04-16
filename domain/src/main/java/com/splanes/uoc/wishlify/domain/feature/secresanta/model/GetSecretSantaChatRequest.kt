package com.splanes.uoc.wishlify.domain.feature.secresanta.model

sealed class GetSecretSantaChatRequest(
  open val eventId: String,
  open val otherUid: String,
) {

  data class AsReceiver(
    override val eventId: String,
    override val otherUid: String
  ) : GetSecretSantaChatRequest(
    eventId = eventId,
    otherUid = otherUid
  )

  data class AsGiver(
    override val eventId: String,
    override val otherUid: String
  ) : GetSecretSantaChatRequest(
    eventId = eventId,
    otherUid = otherUid
  )
}