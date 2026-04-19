package com.splanes.uoc.wishlify.domain.feature.secretsanta.helper

class SecretSantaChatIdBuilder {

  fun build(
    giver: String,
    receiver: String,
  ): String = "${giver}_$receiver"
}