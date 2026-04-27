package com.splanes.uoc.wishlify.domain.feature.secretsanta.helper

/**
 * Builds stable chat identifiers for Secret Santa conversations.
 *
 * Chat ids are derived from the giver and receiver identifiers so both sides
 * resolve the same conversation key.
 */
class SecretSantaChatIdBuilder {

  /** Builds the chat id for the given giver and receiver pair. */
  fun build(
    giver: String,
    receiver: String,
  ): String = "${giver}_$receiver"
}
