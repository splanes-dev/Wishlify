package com.splanes.uoc.wishlify.data.common.firebase.utils.db

import com.google.firebase.firestore.FirebaseFirestore

val FirebaseFirestore.users
  get() = collection(Collections.USERS)

private object Collections {
  const val USERS = "users"
}