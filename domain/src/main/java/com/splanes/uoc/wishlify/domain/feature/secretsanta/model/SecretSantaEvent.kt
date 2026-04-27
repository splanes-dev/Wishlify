package com.splanes.uoc.wishlify.domain.feature.secretsanta.model

import java.time.Instant
import java.util.Date

/** Lightweight projection of a Secret Santa event used in listings. */
sealed class SecretSantaEvent(
  open val id: String,
  open val name: String,
  open val photoUrl: String?,
  open val group: String?,
  open val deadline: Date
) {

  /** Whether the event deadline has already passed. */
  fun isFinished() =
    deadline.toInstant().isBefore(Instant.now())

  /** Event state before the draw has been executed. */
  data class DrawPending(
    override val id: String,
    override val name: String,
    override val photoUrl: String?,
    override val group: String?,
    override val deadline: Date
  ) : SecretSantaEvent(
    id = id,
    name = name,
    photoUrl = photoUrl,
    group = group,
    deadline = deadline
  )

  /** Event state after the draw has been executed. */
  data class DrawDone(
    override val id: String,
    override val name: String,
    override val photoUrl: String?,
    override val group: String?,
    override val deadline: Date,
    val target: String
  ) : SecretSantaEvent(
    id = id,
    name = name,
    photoUrl = photoUrl,
    group = group,
    deadline = deadline
  )
}
