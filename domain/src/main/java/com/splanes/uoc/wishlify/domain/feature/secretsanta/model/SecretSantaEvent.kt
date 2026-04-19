package com.splanes.uoc.wishlify.domain.feature.secretsanta.model

import java.time.Instant
import java.util.Date

sealed class SecretSantaEvent(
  open val id: String,
  open val name: String,
  open val photoUrl: String?,
  open val deadline: Date
) {

  fun isFinished() =
    deadline.toInstant().isBefore(Instant.now())

  data class DrawPending(
    override val id: String,
    override val name: String,
    override val photoUrl: String?,
    override val deadline: Date
  ) : SecretSantaEvent(
    id = id,
    name = name,
    photoUrl = photoUrl,
    deadline = deadline
  )

  data class DrawDone(
    override val id: String,
    override val name: String,
    override val photoUrl: String?,
    override val deadline: Date,
    val target: String
  ) : SecretSantaEvent(
    id = id,
    name = name,
    photoUrl = photoUrl,
    deadline = deadline
  )
}