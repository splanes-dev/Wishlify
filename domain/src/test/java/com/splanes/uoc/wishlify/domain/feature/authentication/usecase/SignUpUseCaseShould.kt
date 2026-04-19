package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SignUpRequest
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

class SignUpUseCaseShould {

  private val authRepository: AuthenticationRepository = mock()
  private val userRepository: UserRepository = mock()

  private lateinit var useCase: SignUpUseCase

  @Before
  fun setup() {
    useCase = SignUpUseCase(
      authRepository = authRepository,
      userRepository = userRepository,
    )
  }

  @Test
  fun `return failure when sign up fails`() = runTest {
    val request = SignUpRequest(
      username = "sergi",
      email = "test@test.com",
      password = "123456"
    )
    val error = RuntimeException()

    whenever(authRepository.signUp(request.email, request.password))
      .thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(userRepository, never()).addUser(any(), any(), anyOrNull())
    verify(authRepository, never()).storeCredentials(request.email, request.password)
  }

  @Test
  fun `return failure when add user fails`() = runTest {
    val request = SignUpRequest(
      username = "sergi",
      email = "test@test.com",
      password = "123456"
    )
    val uid = "uid"
    val error = RuntimeException()

    whenever(authRepository.signUp(request.email, request.password))
      .thenReturn(Result.success(uid))
    whenever(userRepository.addUser(uid, request.username))
      .thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(userRepository).addUser(uid, request.username)
    verify(authRepository, never()).storeCredentials(request.email, request.password)
  }

  @Test
  fun `store credentials when sign up and add user succeed`() = runTest {
    val request = SignUpRequest(
      username = "sergi",
      email = "test@test.com",
      password = "123456"
    )
    val uid = "uid"

    whenever(authRepository.signUp(request.email, request.password))
      .thenReturn(Result.success(uid))
    whenever(userRepository.addUser(uid, request.username))
      .thenReturn(Result.success(Unit))
    whenever(authRepository.storeCredentials(request.email, request.password))
      .thenReturn(Unit)

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(userRepository).addUser(uid, request.username)
    verify(authRepository).storeCredentials(request.email, request.password)
  }

  @Test
  fun `return success when store credentials fails because result is ignored`() = runTest {
    val request = SignUpRequest(
      username = "sergi",
      email = "test@test.com",
      password = "123456"
    )
    val uid = "uid"

    whenever(authRepository.signUp(request.email, request.password))
      .thenReturn(Result.success(uid))
    whenever(userRepository.addUser(uid, request.username))
      .thenReturn(Result.success(Unit))
    whenever(authRepository.storeCredentials(request.email, request.password))
      .thenThrow(RuntimeException())

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    verify(authRepository).storeCredentials(request.email, request.password)
  }

  @Test
  fun `return success when everything succeeds`() = runTest {
    val request = SignUpRequest(
      username = "sergi",
      email = "test@test.com",
      password = "123456"
    )
    val uid = "uid"

    whenever(authRepository.signUp(request.email, request.password))
      .thenReturn(Result.success(uid))
    whenever(userRepository.addUser(uid, request.username))
      .thenReturn(Result.success(Unit))
    whenever(authRepository.storeCredentials(request.email, request.password))
      .thenReturn(Unit)

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(authRepository).signUp(request.email, request.password)
    verify(userRepository).addUser(uid, request.username)
    verify(authRepository).storeCredentials(request.email, request.password)
  }
}