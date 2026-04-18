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

class AuthRemoteDataSource(
  private val firebaseAuth: FirebaseAuth
) {

  fun isLogged(): Boolean =
    firebaseAuth.currentUser != null

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

  fun signOut() {
    firebaseAuth.signOut()
  }
}