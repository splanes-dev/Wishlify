package com.splanes.uoc.wishlify.domain.feature.user.model

/**
 * Domain representation of an application user.
 *
 * Different projections are exposed depending on the feature needs.
 */
sealed class User(
  open val uid: String,
  open val username: String,
  open val code: String,
  open val photoUrl: String?
) {

  /** Lightweight user projection used across collaborative features. */
  data class Basic(
    override val uid: String,
    override val username: String,
    override val code: String,
    override val photoUrl: String?
  ) : User(
    uid = uid,
    username = username,
    code = code,
    photoUrl = photoUrl,
  )

  /** Profile projection focused on account and identity information. */
  data class BasicProfile(
    override val uid: String,
    override val username: String,
    override val code: String,
    override val photoUrl: String?,
    val points: Int,
    val email: String,
    val isSocialAccount: Boolean,
  ) : User(
    uid = uid,
    username = username,
    code = code,
    photoUrl = photoUrl,
  )

  /** Profile projection focused on gifting interests and hobbies. */
  data class HobbiesProfile(
    override val uid: String,
    override val username: String,
    override val code: String,
    override val photoUrl: String?,
    val hobbies: Hobbies
  ) : User(
    uid = uid,
    username = username,
    code = code,
    photoUrl = photoUrl,
  )

  /** Profile projection focused on notification preferences. */
  data class NotificationsProfile(
    override val uid: String,
    override val username: String,
    override val code: String,
    override val photoUrl: String?,
    val notificationPermissions: NotificationPermissions,
  ) : User(
    uid = uid,
    username = username,
    code = code,
    photoUrl = photoUrl,
  )
}
