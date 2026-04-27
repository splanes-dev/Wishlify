package com.splanes.uoc.wishlify.domain.feature.user.utils

/** Generates a public user code with the `W` prefix used by the application. */
fun newUserCode(): String {
  val suffix = buildString(capacity = CodeLength) {
    repeat(CodeLength) {
      append(Univers.random())
    }
  }
  return "$Prefix$suffix"
}

private const val CodeLength = 5
private const val Prefix = 'W'
private const val Univers = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
