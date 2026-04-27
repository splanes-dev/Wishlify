package com.splanes.uoc.wishlify.domain.common.media.model

/**
 * Resolved image reference used by the domain after the media source has been
 * normalized or persisted.
 */
sealed interface ImageMedia {
  data class Url(val url: String): ImageMedia
  data class Preset(val id: String): ImageMedia
}

/**
 * Input source used when a feature requests an image to be attached or stored.
 *
 * Unlike [ImageMedia], this type models where the image currently comes from.
 */
sealed interface ImageMediaRequest {
  data class Url(val url: String): ImageMediaRequest
  data class Device(val uri: String): ImageMediaRequest
  data class Preset(val id: String): ImageMediaRequest
}
