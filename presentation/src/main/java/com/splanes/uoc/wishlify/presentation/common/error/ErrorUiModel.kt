package com.splanes.uoc.wishlify.presentation.common.error

import androidx.compose.ui.graphics.vector.ImageVector

data class ErrorUiModel(
  val icon: ImageVector,
  val title: String,
  val description: String,
  val dismissButton: String,
  val actionButton: String? = null
)