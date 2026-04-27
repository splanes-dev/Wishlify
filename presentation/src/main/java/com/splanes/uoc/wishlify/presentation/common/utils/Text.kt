package com.splanes.uoc.wishlify.presentation.common.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import java.util.Locale.getDefault

/** Capitalizes only the first character when it is lowercase. */
fun String.capitalize(): String =
  replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }

/** Joins a string list using a dedicated separator before the last element. */
fun List<String>.joinToStringLast(
  lastSeparator: String,
  separator: String = ", ",
): String = when (size) {
  0 -> ""
  1 -> first()
  2 -> first() + lastSeparator + last()
  else -> dropLast(1).joinToString(separator) + lastSeparator + last()
}

/** Returns the localized string resource parsed as HTML-formatted text. */
@Composable
fun htmlString(@StringRes id: Int): AnnotatedString =
  AnnotatedString.fromHtml(stringResource(id))

/** Returns the formatted localized string resource parsed as HTML-formatted text. */
@Composable
fun htmlString(@StringRes id: Int, vararg formatArgs: Any): AnnotatedString =
  AnnotatedString.fromHtml(stringResource(id, *formatArgs))
