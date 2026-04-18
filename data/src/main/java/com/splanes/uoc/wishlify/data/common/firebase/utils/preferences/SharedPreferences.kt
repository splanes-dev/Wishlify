package com.splanes.uoc.wishlify.data.common.firebase.utils.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.json.Json
import timber.log.Timber

inline fun <reified T> SharedPreferences.writeJson(commit: Boolean = false, key: String, value: T) {
  runCatching {
    val json = Json.encodeToString(value)
    edit(commit) { putString(key, json) }
  }.onFailure { error ->
    Timber.e(error)
  }
}

inline fun <reified T> SharedPreferences.readJson(key: String): T? =
  runCatching {
    val json = getString(key, null)
    if (json != null) {
      Json.decodeFromString<T>(json)
    } else {
      null
    }
  }.getOrNull()