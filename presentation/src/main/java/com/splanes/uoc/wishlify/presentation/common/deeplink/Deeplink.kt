package com.splanes.uoc.wishlify.presentation.common.deeplink

import kotlinx.serialization.Serializable

@Serializable
sealed interface Deeplink {
  val token: String

  @Serializable
  @JvmInline
  value class WishlistEditor(override val token: String): Deeplink

  @Serializable
  @JvmInline
  value class WishlistShare(override val token: String): Deeplink

  @Serializable
  @JvmInline
  value class SecretSanta(override val token: String): Deeplink
}