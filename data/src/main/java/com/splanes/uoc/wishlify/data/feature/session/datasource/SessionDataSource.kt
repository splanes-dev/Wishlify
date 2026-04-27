package com.splanes.uoc.wishlify.data.feature.session.datasource

import com.google.firebase.auth.FirebaseAuth
import com.splanes.uoc.wishlify.domain.feature.session.error.SessionError
import com.splanes.uoc.wishlify.domain.feature.session.model.SessionState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Firebase Auth backed data source for session state.
 *
 * It exposes the current authenticated user id and a reactive stream of auth
 * state changes translated into domain session states.
 */
class SessionDataSource(
  private val firebaseAuth: FirebaseAuth,
) {

  /** Returns the current authenticated user id or throws when there is no session. */
  fun getCurrentUserUidOrThrow(): String =
    firebaseAuth.currentUser?.uid ?: throw SessionError.NoSession()

  /** Observes Firebase authentication changes as a distinct domain session flow. */
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
