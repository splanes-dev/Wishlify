package com.splanes.uoc.wishlify.data.feature.authentication.datasource

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.splanes.uoc.wishlify.data.feature.authentication.model.Email
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.feature.authentication.error.SignInError
import com.splanes.uoc.wishlify.domain.feature.authentication.error.SignUpError
import kotlinx.coroutines.tasks.await
import java.net.UnknownHostException

/**
 * Firebase Auth-backed data source for remote authentication operations.
 *
 * It translates Firebase-specific failures into domain-facing authentication
 * and generic errors.
 */
class AuthRemoteDataSource(
  private val firebaseAuth: FirebaseAuth
) {

  /** Returns whether there is a currently authenticated Firebase user. */
  fun isLogged(): Boolean =
    firebaseAuth.currentUser != null

  /**
   * Returns the current authenticated email together with whether it belongs
   * to a social account, or `null` when no email-backed user is available.
   */
  fun currentUserEmail(): Email? {
    val user = firebaseAuth.currentUser
    val email = user?.email
    return if (user != null && email != null) {
      Email(
        email = email,
        isSocialAccount = user.providerData.any { provider ->
          provider.providerId == GoogleAuthProvider.PROVIDER_ID
        }
      )
    } else {
      null
    }
  }

  /** Creates a new Firebase email/password account and returns its uid. */
  suspend fun signUp(email: String, password: String): String {
    try {
      val uid = firebaseAuth
        .createUserWithEmailAndPassword(email, password)
        .await()
        ?.user
        ?.uid
      return uid ?: throw SignUpError.Unknown()
    } catch (_: FirebaseAuthUserCollisionException) {
      throw SignUpError.UserCollision()
    } catch (_: FirebaseAuthWeakPasswordException) {
      throw SignUpError.WeakPassword()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      throw GenericError.Unknown(cause = e)
    }
  }

  /** Signs in with a Google ID token and returns the authenticated uid. */
  suspend fun signIn(token: String): String {
    try {
      val credential = GoogleAuthProvider.getCredential(token, null)
      val uid = firebaseAuth
        .signInWithCredential(credential)
        .await()
        ?.user
        ?.uid
      return uid ?: throw SignUpError.Unknown()
    } catch (_: FirebaseAuthUserCollisionException) {
      throw SignUpError.UserCollision()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      throw GenericError.Unknown(cause = e)
    }
  }

  /** Signs in an existing Firebase user with email and password. */
  suspend fun signIn(email: String, password: String) {
    try {
      val user = firebaseAuth
        .signInWithEmailAndPassword(email, password)
        .await()
        ?.user

      if (user == null) throw SignInError.Unknown()
    } catch (_: FirebaseAuthInvalidCredentialsException) {
      throw SignInError.InvalidCredentials()
    } catch (_: FirebaseAuthInvalidUserException) {
      throw SignInError.InvalidEmail()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      throw GenericError.Unknown(cause = e)
    }
  }

  /** Reauthenticates the current Firebase user with email/password credentials. */
  suspend fun reauthenticate(email: String, password: String) {
    try {
      val user = firebaseAuth.currentUser ?: error("Cannot re-auth, user=null")
      val credentials = EmailAuthProvider.getCredential(email, password)

      user
        .reauthenticate(credentials)
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      throw GenericError.Unknown(cause = e)
    }
  }

  /** Starts Firebase's email update flow for the current authenticated user. */
  suspend fun updateEmail(email: String) {
    try {
      val user = firebaseAuth.currentUser ?: error("Cannot update email, user=null")

      user
        .verifyBeforeUpdateEmail(email)
        .await()

    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      throw GenericError.Unknown(cause = e)
    }
  }

  /** Updates the password of the current authenticated Firebase user. */
  suspend fun updatePassword(password: String) {
    try {
      val user = firebaseAuth.currentUser ?: error("Cannot update email, user=null")

      user
        .updatePassword(password)
        .await()

    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      throw GenericError.Unknown(cause = e)
    }
  }

  /** Signs out the current Firebase user. */
  fun signOut() {
    firebaseAuth.signOut()
  }
}
