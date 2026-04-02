package com.splanes.uoc.wishlify.data.feature.session.datasource

import com.google.firebase.auth.FirebaseAuth
import com.splanes.uoc.wishlify.domain.feature.session.error.SessionError
import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class SessionDataSource(
  private val firebaseAuth: FirebaseAuth,
) {

  fun getCurrentUserUidOrThrow(): String =
    firebaseAuth.currentUser?.uid ?: throw SessionError.NoSession()

  fun observeAuthState(): Flow<SessionState> = callbackFlow {

    val listener = FirebaseAuth.AuthStateListener { auth ->
      val state = when {
        auth.currentUser != null -> SessionState.SignedIn
        else -> SessionState.SignedOut
      }
      trySend(state)
    }
    firebaseAuth.addAuthStateListener(listener)

    awaitClose {
      firebaseAuth.removeAuthStateListener(listener)
    }
  }.distinctUntilChanged()
}