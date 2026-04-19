package com.splanes.uoc.wishlify.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import com.splanes.uoc.wishlify.presentation.common.error.ErrorUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

abstract class UnitTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  protected fun errorUiModel(text: String = "error"): ErrorUiModel =
    ErrorUiModel(icon = Icons.Default.Error, title = text, description = "", dismissButton = "")
}


@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
  val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

  override fun starting(description: Description) {
    Dispatchers.setMain(dispatcher)
  }

  override fun finished(description: Description) {
    Dispatchers.resetMain()
  }
}