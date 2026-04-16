package com.splanes.uoc.wishlify.domain.feature.secresanta.helper

class SecretSantaChatIdBuilder {

  fun build(
    giver: String,
    receiver: String,
  ): String = "${giver}_$receiver"
}