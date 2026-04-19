package com.splanes.uoc.wishlify.domain.feature.user.model

sealed class User(
  open val uid: String,
  open val username: String,
  open val code: String,
  open val photoUrl: String?
) {

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