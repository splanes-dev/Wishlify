package com.splanes.uoc.wishlify.domain.common.media.model

sealed interface ImageMedia {
  data class Url(val url: String): ImageMedia
  data class Preset(val id: String): ImageMedia
}

sealed interface ImageMediaRequest {
  data class Url(val url: String): ImageMediaRequest
  data class Device(val uri: String): ImageMediaRequest
  data class Preset(val id: String): ImageMediaRequest
}