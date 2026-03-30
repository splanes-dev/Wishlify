package com.splanes.uoc.wishlify.data.feature.authentication.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.domain.feature.authentication.error.SignUpError
import kotlinx.coroutines.tasks.await
import java.net.UnknownHostException

class AuthRemoteDataSource(
  private val firebaseAuth: FirebaseAuth
) {

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
}