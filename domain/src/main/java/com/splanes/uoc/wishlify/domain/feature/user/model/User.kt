package com.splanes.uoc.wishlify.domain.feature.user.model

sealed class User {

  data class Basic(
    val uid: String,
    val username: String,
    val code: String,
    val photoUrl: String?
  )
}