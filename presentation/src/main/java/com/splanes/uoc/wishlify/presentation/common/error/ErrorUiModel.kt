package com.splanes.uoc.wishlify.presentation.common.error

import androidx.compose.ui.graphics.vector.ImageVector

/** UI-ready description of an error dialog shown in the presentation layer. */
data class ErrorUiModel(
  val icon: ImageVector,
  val title: String,
  val description: String,
  val dismissButton: String,
  val actionButton: String? = null
)
