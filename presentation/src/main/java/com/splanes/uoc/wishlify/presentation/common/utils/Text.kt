package com.splanes.uoc.wishlify.presentation.common.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import java.util.Locale.getDefault

fun String.capitalize(): String =
  replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }

fun List<String>.joinToStringLast(
  lastSeparator: String,
  separator: String = ", ",
): String = when (size) {
  0 -> ""
  1 -> first()
  2 -> first() + lastSeparator + last()
  else -> dropLast(1).joinToString(separator) + lastSeparator + last()
}

@Composable
fun htmlString(@StringRes id: Int): AnnotatedString =
  AnnotatedString.fromHtml(stringResource(id))

@Composable
fun htmlString(@StringRes id: Int, vararg formatArgs: Any): AnnotatedString =
  AnnotatedString.fromHtml(stringResource(id, *formatArgs))