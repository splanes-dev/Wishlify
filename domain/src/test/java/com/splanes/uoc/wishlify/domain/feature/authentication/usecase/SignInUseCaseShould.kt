package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.authentication.model.SignInRequest
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SignInUseCaseShould {

  private val authRepository: AuthenticationRepository = mock()

  private lateinit var useCase: SignInUseCase

  @Before
  fun setup() {
    useCase = SignInUseCase(authRepository)
  }

  @Test
  fun `return failure when sign in fails`() = runTest {
    val request = SignInRequest(
      email = "test@test.com",
      password = "123456"
    )
    val error = RuntimeException()

    whenever(authRepository.signIn(request.email, request.password))
      .thenReturn(Result.failure(error))

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(authRepository, never()).storeCredentials(request.email, request.password)
  }

  @Test
  fun `store credentials when sign in succeeds`() = runTest {
    val request = SignInRequest(
      email = "test@test.com",
      password = "123456"
    )

    whenever(authRepository.signIn(request.email, request.password))
      .thenReturn(Result.success(Unit))
    whenever(authRepository.storeCredentials(request.email, request.password))
      .thenReturn(Unit)

    val result = useCase(request)

    assertThat(result.isSuccess).isTrue()
    verify(authRepository).signIn(request.email, request.password)
    verify(authRepository).storeCredentials(request.email, request.password)
  }

  @Test
  fun `return success when store credentials fails because result is ignored`() = runTest {
    val request = SignInRequest(
      email = "test@test.com",
      password = "123456"
    )

    whenever(authRepository.signIn(request.email, request.password))
      .thenReturn(Result.success(Unit))
    whenever(authRepository.storeCredentials(request.email, request.password))
      .thenThrow(RuntimeException())

    val result = useCase(request)

    assertThat(result.isFailure).isTrue()
    verify(authRepository).storeCredentials(request.email, request.password)
  }
}