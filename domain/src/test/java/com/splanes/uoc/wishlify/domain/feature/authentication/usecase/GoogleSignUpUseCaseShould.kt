package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SocialCredentials
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GoogleSignUpUseCaseShould {

  private val authRepository: AuthenticationRepository = mock()
  private val userRepository: UserRepository = mock()

  private lateinit var useCase: GoogleSignUpUseCase

  @Before
  fun setup() {
    useCase = GoogleSignUpUseCase(
      authRepository = authRepository,
      userRepository = userRepository
    )
  }

  @Test
  fun `return failure when google sign up fails`() = runTest {
    val error = RuntimeException()

    whenever(authRepository.googleSignUp())
      .thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(authRepository, never()).signIn(any())
  }

  @Test
  fun `return failure when sign in fails`() = runTest {
    val credentials = socialCredentials()
    val error = RuntimeException()

    whenever(authRepository.googleSignUp())
      .thenReturn(Result.success(credentials))
    whenever(authRepository.signIn(credentials.token))
      .thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(userRepository, never()).addUser(
      any(),
      any(),
      anyOrNull()
    )
  }

  @Test
  fun `return failure when add user fails`() = runTest {
    val credentials = socialCredentials()
    val uid = "uid"
    val error = RuntimeException()

    whenever(authRepository.googleSignUp())
      .thenReturn(Result.success(credentials))
    whenever(authRepository.signIn(credentials.token))
      .thenReturn(Result.success(uid))
    whenever(
      userRepository.addUser(uid, credentials.username, credentials.photoUrl)
    ).thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(userRepository).addUser(uid, credentials.username, credentials.photoUrl)
  }

  @Test
  fun `return success when everything succeeds`() = runTest {
    val credentials = socialCredentials()
    val uid = "uid"

    whenever(authRepository.googleSignUp())
      .thenReturn(Result.success(credentials))
    whenever(authRepository.signIn(credentials.token))
      .thenReturn(Result.success(uid))
    whenever(
      userRepository.addUser(uid, credentials.username, credentials.photoUrl)
    ).thenReturn(Result.success(Unit))

    val result = useCase()

    assertThat(result.isSuccess).isTrue()
    verify(authRepository).signIn(credentials.token)
    verify(userRepository).addUser(uid, credentials.username, credentials.photoUrl)
  }

  private fun socialCredentials() = SocialCredentials(
    token = "token",
    username = "sergi",
    photoUrl = "photo-url"
  )
}