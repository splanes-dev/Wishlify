package com.splanes.uoc.wishlify.presentation.common.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.splanes.uoc.wishlify.presentation.R
import timber.log.Timber

fun Context.openBrowserLink(url: String): Boolean {
  try {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    startActivity(intent)
    return true
  } catch (e: Throwable) {
    Timber.e(e)
    Toast
      .makeText(this, R.string.error_open_link, Toast.LENGTH_SHORT)
      .show()
    return false
  }
}