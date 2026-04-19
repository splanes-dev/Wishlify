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

class GoogleSignInUseCaseShould {

  private val authRepository: AuthenticationRepository = mock()
  private val userRepository: UserRepository = mock()

  private lateinit var useCase: GoogleSignInUseCase

  @Before
  fun setup() {
    useCase = GoogleSignInUseCase(
      authRepository = authRepository,
      userRepository = userRepository,
    )
  }

  @Test
  fun `return failure when google sign in fails`() = runTest {
    val error = RuntimeException("google sign in failed")
    whenever(authRepository.googleSignIn()).thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(authRepository, never()).signIn(any())
  }

  @Test
  fun `sign in with token and do nothing else when user already exists`() = runTest {
    val credentials = socialCredentials()
    val uid = "uid"

    whenever(authRepository.googleSignIn()).thenReturn(Result.success(credentials))
    whenever(authRepository.signIn(credentials.token)).thenReturn(Result.success(uid))
    whenever(userRepository.existsUser(uid)).thenReturn(Result.success(true))

    val result = useCase()

    assertThat(result.isSuccess).isTrue()
    verify(authRepository).signIn(credentials.token)
    verify(userRepository).existsUser(uid)
    verify(userRepository, never()).addUser(
      any(),
      any(),
      anyOrNull()
    )
  }

  @Test
  fun `add user when google sign in succeeds and user does not exist`() = runTest {
    val credentials = socialCredentials()
    val uid = "uid"

    whenever(authRepository.googleSignIn()).thenReturn(Result.success(credentials))
    whenever(authRepository.signIn(credentials.token)).thenReturn(Result.success(uid))
    whenever(userRepository.existsUser(uid)).thenReturn(Result.success(false))
    whenever(
      userRepository.addUser(uid, credentials.username, credentials.photoUrl)
    ).thenReturn(Result.success(Unit))

    val result = useCase()

    assertThat(result.isSuccess).isTrue()
    verify(authRepository).signIn(credentials.token)
    verify(userRepository).existsUser(uid)
    verify(userRepository).addUser(uid, credentials.username, credentials.photoUrl)
  }

  @Test
  fun `return failure when sign in with token fails`() = runTest {
    val credentials = socialCredentials()
    val error = RuntimeException("token sign in failed")

    whenever(authRepository.googleSignIn()).thenReturn(Result.success(credentials))
    whenever(authRepository.signIn(credentials.token)).thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(userRepository, never()).existsUser(any())
    verify(userRepository, never()).addUser(
      any(),
      any(),
      anyOrNull()
    )
  }

  @Test
  fun `return failure when exists user check fails`() = runTest {
    val credentials = socialCredentials()
    val uid = "uid"
    val error = RuntimeException("exists user failed")

    whenever(authRepository.googleSignIn()).thenReturn(Result.success(credentials))
    whenever(authRepository.signIn(credentials.token)).thenReturn(Result.success(uid))
    whenever(userRepository.existsUser(uid)).thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(userRepository).existsUser(uid)
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
    val error = RuntimeException("add user failed")

    whenever(authRepository.googleSignIn()).thenReturn(Result.success(credentials))
    whenever(authRepository.signIn(credentials.token)).thenReturn(Result.success(uid))
    whenever(userRepository.existsUser(uid)).thenReturn(Result.success(false))
    whenever(
      userRepository.addUser(uid, credentials.username, credentials.photoUrl)
    ).thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(userRepository).addUser(uid, credentials.username, credentials.photoUrl)
  }

  private fun socialCredentials() = SocialCredentials(
    token = "token",
    username = "sergi",
    photoUrl = "photo-url"
  )
}