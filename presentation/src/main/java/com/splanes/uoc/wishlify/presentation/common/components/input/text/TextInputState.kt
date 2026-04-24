package com.splanes.uoc.wishlify.presentation.common.components.input.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class TextInputState(
  initialValue: String = "",
  private val supportingText: String = "",
  private val onClearError: () -> Unit = {},
) {
  var text by mutableStateOf(initialValue)
    private set

  var isError by mutableStateOf(false)
    private set

  var support by mutableStateOf(supportingText)
    private set

  fun onValueChanged(text: String) {
    this.text = text
    this.isError = false
    this.support = supportingText
    onClearError()
  }

  fun onClear() {
    onValueChanged(text = "")
  }

  fun error(text: String?) {
    this.isError = text != null
    text?.let { t -> this.support = t }
    if (!this.isError) onClearError()
  }

  fun validate(validation: (String) -> String?): Boolean {
    val err = validation(text)
    isError = err != null
    support = err ?: supportingText
    if (!this.isError) onClearError()
    return err != null
  }
}

@Composable
fun rememberTextInputState(
  initialValue: String = "",
  supportingText: String = "",
  onClearError: () -> Unit = {}
)  = remember(initialValue) { TextInputState(initialValue, supportingText, onClearError) }