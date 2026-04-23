package com.splanes.uoc.wishlify.data.feature.user.mapper

import com.splanes.uoc.wishlify.data.common.utils.nowInMillis
import com.splanes.uoc.wishlify.data.feature.authentication.model.Email
import com.splanes.uoc.wishlify.data.feature.user.model.UserBasic
import com.splanes.uoc.wishlify.data.feature.user.model.UserEntity
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMedia
import com.splanes.uoc.wishlify.domain.feature.user.model.Hobbies
import com.splanes.uoc.wishlify.domain.feature.user.model.NotificationPermissions
import com.splanes.uoc.wishlify.domain.feature.user.model.UpdateProfileRequest
import com.splanes.uoc.wishlify.domain.feature.user.model.User
import com.splanes.uoc.wishlify.domain.feature.user.utils.newUserCode

class UserDataMapper {

  fun map(uid: String, token: String?, username: String, photoUrl: String?): UserEntity =
    UserEntity(
      uid = uid,
      username = username,
      photoUrl = photoUrl,
      code = newUserCode(),
      token = token.orEmpty(),
      hobbies = UserEntity.Hobbies(
        enabled = false,
        values = emptyList()
      ),
      rewards = UserEntity.Rewards(
        points = 0,
        purchased = emptyList()
      ),
      notifications = UserEntity.Notifications(
        sharedWishlistChat = true,
        sharedWishlistUpdates = true,
        sharedWishlistsDeadlineReminders = true,
        secretSantaChat = true,
        secretSantaDeadlineReminders = true,
      ),
      metadata = UserEntity.Metadata(
        createdAt = nowInMillis(),
        lastAccess = nowInMillis()
      )
    )

  fun map(
    base: UserEntity,
    request: UpdateProfileRequest,
    imageMedia: ImageMedia?
  ): UserEntity =
    when (request) {
      is UpdateProfileRequest.BasicInfo -> base.copy(
        photoUrl = imageMedia?.let { media ->
          when (media) {
            is ImageMedia.Preset -> null
            is ImageMedia.Url -> media.url
          }
        },
        username = request.username,
      )

      is UpdateProfileRequest.Hobbies -> base.copy(
        hobbies = UserEntity.Hobbies(
          enabled = request.enabled,
          values = request.values
        )
      )

      is UpdateProfileRequest.Notifications -> base.copy(
        notifications = base.notifications.copy(
          sharedWishlistChat = request.sharedWishlistChat,
          sharedWishlistUpdates = request.sharedWishlistUpdates,
          sharedWishlistsDeadlineReminders = request.sharedWishlistsDeadlineReminders,
          secretSantaChat = request.secretSantaChat,
          secretSantaDeadlineReminders = request.secretSantaDeadlineReminders,
        )
      )
    }

  fun mapToBasic(entity: UserEntity): UserBasic =
    UserBasic(
      uid = entity.uid,
      username = entity.username,
      code = entity.code,
      photoUrl = entity.photoUrl
    )

  fun map(basic: UserBasic): User.Basic =
    User.Basic(
      uid = basic.uid,
      username = basic.username,
      code = basic.code,
      photoUrl = basic.photoUrl
    )

  fun mapToBasicProfile(
    entity: UserEntity,
    email: Email
  ): User.BasicProfile =
    User.BasicProfile(
      uid = entity.uid,
      username = entity.username,
      code = entity.code,
      photoUrl = entity.photoUrl,
      points = entity.rewards.points,
      email = email.email,
      isSocialAccount = email.isSocialAccount,
    )

  fun mapToHobbiesProfile(
    entity: UserEntity
  ): User.HobbiesProfile =
    User.HobbiesProfile(
      uid = entity.uid,
      username = entity.username,
      code = entity.code,
      photoUrl = entity.photoUrl,
      hobbies = Hobbies(
        enabled = entity.hobbies.enabled,
        values = entity.hobbies.values
      )
    )

  fun mapToNotificationsProfile(entity: UserEntity): User.NotificationsProfile =
    User.NotificationsProfile(
      uid = entity.uid,
      username = entity.username,
      code = entity.code,
      photoUrl = entity.photoUrl,
      notificationPermissions = NotificationPermissions(
        sharedWishlistChat = entity.notifications.sharedWishlistChat,
        sharedWishlistUpdates = entity.notifications.sharedWishlistUpdates,
        sharedWishlistsDeadlineReminders = entity.notifications.sharedWishlistsDeadlineReminders,
        secretSantaChat = entity.notifications.secretSantaChat,
        secretSantaDeadlineReminders = entity.notifications.secretSantaDeadlineReminders,
      ),
    )
}