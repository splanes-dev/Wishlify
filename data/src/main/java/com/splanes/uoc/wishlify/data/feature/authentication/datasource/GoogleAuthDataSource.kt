package com.splanes.uoc.wishlify.data.feature.authentication.datasource

import android.content.Context
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.splanes.uoc.wishlify.data.R
import com.splanes.uoc.wishlify.data.feature.authentication.model.GoogleCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.error.SignUpError
import java.security.SecureRandom

class GoogleAuthDataSource(private val context: Context) {

  private val credentialsManager by lazy { CredentialManager.create(context) }

  suspend fun getSignUpCredentials(): GoogleCredentials {
    return getCredentials(filterByAuthorizedAccounts = false)
  }

  private suspend fun getCredentials(filterByAuthorizedAccounts: Boolean): GoogleCredentials {
    val googleIdOption = GetGoogleIdOption.Builder()
      .setServerClientId(context.getString(R.string.google_web_client_id))
      .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
      .setAutoSelectEnabled(true)
      .setNonce(generateSecureRandomNonce())
      .build()

    val request = GetCredentialRequest.Builder()
      .addCredentialOption(googleIdOption)
      .build()

    val response = credentialsManager.getCredential(context, request)

    return when (val credential = response.credential) {
      is CustomCredential -> {
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val credentials = GoogleIdTokenCredential.createFrom(credential.data)
          GoogleCredentials(
            token = credentials.idToken,
            username = credentials.displayName ?: credentials.uniqueId,
            photoUrl = credentials.profilePictureUri
              ?.takeIf { uri -> uri.scheme in listOf("http", "https") }
              ?.toString(),
          )
        } else {
          throw SignUpError.GoogleSignUpFailed()
        }
      }
      else -> {
        throw SignUpError.GoogleSignUpFailed()
      }
    }
  }

  private fun generateSecureRandomNonce(byteLength: Int = 32): String {
    val randomBytes = ByteArray(byteLength)
    SecureRandom().nextBytes(randomBytes)
    return Base64.encodeToString(randomBytes, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)
  }
}