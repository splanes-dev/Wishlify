package com.splanes.uoc.wishlify.presentation.feature.secretsanta.feature.chat.model

enum class SecretSantaChatType(val value: String) {
  AsReceiver("as_receiver"),
  AsGiver("as_giver")
  ;

  companion object {
    fun from(value: String) = entries.first { it.value == value }
  }
}