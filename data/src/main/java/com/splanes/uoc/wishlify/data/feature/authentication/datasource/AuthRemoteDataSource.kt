package com.splanes.uoc.wishlify.data.feature.authentication.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
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
}