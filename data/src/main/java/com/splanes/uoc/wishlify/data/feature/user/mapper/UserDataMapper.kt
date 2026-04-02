package com.splanes.uoc.wishlify.data.feature.user.mapper

import com.splanes.uoc.wishlify.data.feature.user.model.UserBasic
import com.splanes.uoc.wishlify.data.feature.user.model.UserEntity
import com.splanes.uoc.wishlify.domain.feature.user.model.User

class UserDataMapper {

  fun map(uid: String, username: String, photoUrl: String?): UserEntity =
    UserEntity(
      uid = uid,
      username = username,
      photoUrl = photoUrl,
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
        createdAt = System.currentTimeMillis(),
        lastAccess = System.currentTimeMillis()
      )
    )

  fun mapToBasic(entity: UserEntity): UserBasic =
    UserBasic(
      uid = entity.uid,
      username = entity.username
    )

  fun map(basic: UserBasic): User.Basic =
    User.Basic(
      uid = basic.uid,
      username = basic.username
    )
}