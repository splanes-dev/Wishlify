package com.splanes.uoc.wishlify.data.common.firebase.utils.db

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

// Collections
val FirebaseFirestore.users
  get() = collection(Collections.USERS)

val FirebaseFirestore.wishlists
  get() = collection(Collections.WISHLISTS)

// Subcollections
val DocumentReference.wishlistCategories
  get() = collection(Subcollections.CATEGORIES)

val DocumentReference.wishlistItems
  get() = collection(Subcollections.WISHLIST_ITEMS)

private object Collections {
  const val USERS = "users"
  const val WISHLISTS = "wishlists"
}

private object Subcollections {
  const val CATEGORIES = "wishlist-categories"
  const val WISHLIST_ITEMS = "items"
}