package com.splanes.uoc.wishlify.presentation.common.error

import android.content.Context
import android.content.res.Resources
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.SignalWifiConnectedNoInternet4
import androidx.compose.material.icons.rounded.TimerOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import com.splanes.uoc.wishlify.presentation.R

open class ErrorUiMapper(context: Context) {

  protected val res: Resources by lazy { context.resources }

  fun map(error: Throwable): ErrorUiModel =
    when (error) {
      is GenericError.NoInternet -> {
        errorOf(
          icon = Icons.Rounded.SignalWifiConnectedNoInternet4,
          title = R.string.error_dialog_title_oops,
          description = R.string.error_dialog_description_no_internet,
        )
      }

      is GenericError.RequestTimeout -> {
        errorOf(
          icon = Icons.Rounded.TimerOff,
          title = R.string.error_dialog_title_tick_tack,
          description = R.string.error_dialog_description_timeout,
        )
      }

      is GenericError.InternalServerError -> {
        errorOf(
          icon = Icons.Rounded.CloudOff,
          title = R.string.error_dialog_title_oops,
          description = R.string.error_dialog_description_internal_server_error,
        )
      }
      /* GenericError.Unknown & other exceptions */ else -> {
        errorOf(
          icon = Icons.Rounded.ErrorOutline,
          title = R.string.error_dialog_title_oops,
          description = R.string.error_dialog_description_generic,
        )
      }
    }

  protected fun errorOf(
    icon: ImageVector,
    title: Int,
    description: Int,
    dismissButton: Int = R.string.error_dialog_dismiss_button_default,
    actionButton: Int? = null,
  ) = ErrorUiModel(
    icon = icon,
    title = res.getString(title),
    description = res.getString(description),
    dismissButton = res.getString(dismissButton),
    actionButton = actionButton?.let(res::getString),
  )
}