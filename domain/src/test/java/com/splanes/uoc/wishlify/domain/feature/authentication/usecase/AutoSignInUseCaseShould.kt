package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.authentication.error.SignInError
import com.splanes.uoc.wishlify.domain.feature.authentication.model.LocalCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AutoSignInUseCaseShould {

  private val authRepository: AuthenticationRepository = mock()

  private lateinit var useCase: AutoSignInUseCase

  @Before
  fun setup() {
    useCase = AutoSignInUseCase(authRepository)
  }

  @Test
  fun `return success when user is already signed in`() = runTest {
    whenever(authRepository.isSignedIn()).thenReturn(true)

    val result = useCase()

    assertThat(result.isSuccess).isTrue()
    verify(authRepository, never()).fetchStoredCredentials()
  }

  @Test
  fun `sign in with stored credentials when user is not signed in and credentials exist`() = runTest {
    val credentials = LocalCredentials(
      email = "test@test.com",
      password = "123456"
    )

    whenever(authRepository.isSignedIn()).thenReturn(false)
    whenever(authRepository.fetchStoredCredentials()).thenReturn(credentials)
    whenever(authRepository.signIn(credentials.email, credentials.password))
      .thenReturn(Result.success(Unit))

    val result = useCase()

    assertThat(result.isSuccess).isTrue()
    verify(authRepository).signIn(credentials.email, credentials.password)
    verify(authRepository, never()).cleanStoredCredentials()
  }

  @Test
  fun `return auto sign in failed when user is not signed in and no stored credentials exist`() = runTest {
    whenever(authRepository.isSignedIn()).thenReturn(false)
    whenever(authRepository.fetchStoredCredentials()).thenReturn(null)

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(SignInError.AutoSignInFailed::class.java)
    verify(authRepository, never()).signIn(anyString(), anyString())
  }

  @Test
  fun `clean stored credentials when sign in fails with invalid email`() = runTest {
    val credentials = LocalCredentials(
      email = "bad@test.com",
      password = "123456"
    )
    val error = SignInError.InvalidEmail()

    whenever(authRepository.isSignedIn()).thenReturn(false)
    whenever(authRepository.fetchStoredCredentials()).thenReturn(credentials)
    whenever(authRepository.signIn(credentials.email, credentials.password))
      .thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isEqualTo(error)
    verify(authRepository).cleanStoredCredentials()
  }

  @Test
  fun `clean stored credentials when sign in fails with invalid credentials`() = runTest {
    val credentials = LocalCredentials(
      email = "test@test.com",
      password = "wrong"
    )
    val error = SignInError.InvalidCredentials()

    whenever(authRepository.isSignedIn()).thenReturn(false)
    whenever(authRepository.fetchStoredCredentials()).thenReturn(credentials)
    whenever(authRepository.signIn(credentials.email, credentials.password))
      .thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isEqualTo(error)
    verify(authRepository).cleanStoredCredentials()
  }

  @Test
  fun `not clean stored credentials when sign in fails with other error`() = runTest {
    val credentials = LocalCredentials(
      email = "test@test.com",
      password = "123456"
    )
    val error = RuntimeException()

    whenever(authRepository.isSignedIn()).thenReturn(false)
    whenever(authRepository.fetchStoredCredentials()).thenReturn(credentials)
    whenever(authRepository.signIn(credentials.email, credentials.password))
      .thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(authRepository, never()).cleanStoredCredentials()
  }

  private fun anyString(): String = any()
}