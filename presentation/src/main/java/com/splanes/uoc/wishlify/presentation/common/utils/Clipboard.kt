package com.splanes.uoc.wishlify.presentation.common.utils

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry

/** Copies plain text into the Compose clipboard using the provided user-facing label. */
suspend fun Clipboard.copyToClipboard(
  label: String,
  text: String
) {
  val clipData = ClipData.newPlainText(label, text)
  setClipEntry(clipData.toClipEntry())
}
