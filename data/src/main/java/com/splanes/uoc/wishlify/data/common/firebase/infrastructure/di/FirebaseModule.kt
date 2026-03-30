package com.splanes.uoc.wishlify.data.common.firebase.infrastructure.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import org.koin.dsl.module

internal val FirebaseModule = module {
  // Firebase Auth
  single { FirebaseAuth.getInstance() }
  // Firebase Firestore
  single { Firebase.firestore }
}