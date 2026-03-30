package com.splanes.uoc.wishlify.data.feature.user.mapper

import com.splanes.uoc.wishlify.data.feature.user.model.UserDto

class UserDataMapper {

  fun map(uid: String, username: String, photoUrl: String?): UserDto =
    UserDto(
      uid = uid,
      username = username,
      photoUrl = photoUrl,
      hobbies = UserDto.HobbiesDto(
        enabled = false,
        values = emptyList()
      ),
      rewards = UserDto.RewardsDto(
        points = 0,
        purchased = emptyList()
      ),
      notifications = UserDto.NotificationsDto(
        sharedWishlistChat = true,
        sharedWishlistUpdates = true,
        sharedWishlistsDeadlineReminders = true,
        secretSantaChat = true,
        secretSantaDeadlineReminders = true,
      ),
      metadata = UserDto.MetadataDto(
        createdAt = System.currentTimeMillis(),
        lastAccess = System.currentTimeMillis()
      )
    )
}