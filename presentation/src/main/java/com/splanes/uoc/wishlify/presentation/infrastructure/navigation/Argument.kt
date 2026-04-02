package com.splanes.uoc.wishlify.presentation.infrastructure.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T> argumentTypeMapOf(): Map<KType, NavType<T>> =
  object : NavType<T>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): T? =
      bundle.getString(key)?.let(Json::decodeFromString)

    override fun parseValue(value: String): T =
      Json.decodeFromString<T>(value)

    override fun put(bundle: Bundle, key: String, value: T) =
      bundle.putString(key, Json.encodeToString(value))

    override fun serializeAsValue(value: T): String =
      Uri.encode(Json.encodeToString(value))
  }.let { navType ->
    mapOf(typeOf<T>() to navType)
  }