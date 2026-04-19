package com.splanes.uoc.wishlify.domain.feature.authentication.usecase

import com.google.common.truth.Truth.assertThat
import com.splanes.uoc.wishlify.domain.feature.authentication.repository.AuthenticationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SignOutUseCaseShould {

  private val repository: AuthenticationRepository = mock()

  private lateinit var useCase: SignOutUseCase

  @Before
  fun setup() {
    useCase = SignOutUseCase(repository)
  }

  @Test
  fun `return failure when sign out fails`() = runTest {
    val error = RuntimeException()

    whenever(repository.signOut()).thenReturn(Result.failure(error))

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    verify(repository, never()).cleanStoredCredentials()
  }

  @Test
  fun `clean stored credentials when sign out succeeds`() = runTest {
    whenever(repository.signOut()).thenReturn(Result.success(Unit))
    whenever(repository.cleanStoredCredentials()).thenReturn(Unit)

    val result = useCase()

    assertThat(result.isSuccess).isTrue()
    verify(repository).signOut()
    verify(repository).cleanStoredCredentials()
  }

  @Test
  fun `return success when clean stored credentials fails because result is ignored`() = runTest {
    whenever(repository.signOut()).thenReturn(Result.success(Unit))
    whenever(repository.cleanStoredCredentials()).thenThrow(RuntimeException())

    val result = useCase()

    assertThat(result.isFailure).isTrue()
    verify(repository).cleanStoredCredentials()
  }
}