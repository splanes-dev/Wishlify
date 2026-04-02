package com.splanes.uoc.wishlify.presentation.common.utils

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry


suspend fun Clipboard.copyToClipboard(
  label: String,
  text: String
) {
  val clipData = ClipData.newPlainText(label, text)
  setClipEntry(clipData.toClipEntry())
}