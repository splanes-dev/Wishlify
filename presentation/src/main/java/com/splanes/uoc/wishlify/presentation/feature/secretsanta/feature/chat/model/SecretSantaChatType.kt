package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.model

/**
 * Perspective from which the current user is participating in the anonymous Secret Santa chat.
 */
enum class SecretSantaChatType(val value: String) {
  AsReceiver("as_receiver"),
  AsGiver("as_giver")
  ;

  companion object {
    /**
     * Resolves the enum value encoded in navigation arguments.
     */
    fun from(value: String) = entries.first { it.value == value }
  }
}
