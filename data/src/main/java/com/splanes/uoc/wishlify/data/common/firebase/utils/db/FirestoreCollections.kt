package com.splanes.uoc.wishlify.data.common.firebase.utils.db

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

// Collections
val FirebaseFirestore.users
  get() = collection(Collections.USERS)

val FirebaseFirestore.wishlists
  get() = collection(Collections.WISHLISTS)

val FirebaseFirestore.sharedWishlists
  get() = collection(Collections.SHARED_WISHLISTS)

val FirebaseFirestore.secretSanta
  get() = collection(Collections.SECRET_SANTA)

val FirebaseFirestore.groups
  get() = collection(Collections.GROUPS)

val FirebaseFirestore.systemUidByEmail
  get() = collection(Collections.SYSTEM_UID_BY_MAIL)

// Subcollections
val DocumentReference.wishlistCategories
  get() = collection(Subcollections.CATEGORIES)

val DocumentReference.wishlistItems
  get() = collection(Subcollections.WISHLIST_ITEMS)

val DocumentReference.sharedWishlistItems
  get() = collection(Subcollections.SHARED_WISHLIST_ITEMS)

val DocumentReference.sharedWishlistChatMessages
  get() = collection(Subcollections.SHARED_WISHLIST_CHAT_MESSAGES)

val DocumentReference.secretSantaAssignments
  get() = collection(Subcollections.SECRET_SANTA_ASSIGNMENTS)

val DocumentReference.secretSantaParticipantsWishlist
  get() = collection(Subcollections.SECRET_SANTA_PARTICIPANTS_WISHLIST)

val DocumentReference.secretSantaParticipantsWishlistItems
  get() = collection(Subcollections.SECRET_SANTA_PARTICIPANTS_WISHLIST_ITEMS)


private object Collections {
  const val USERS = "users"
  const val WISHLISTS = "wishlists"
  const val SHARED_WISHLISTS = "shared-wishlists"
  const val SECRET_SANTA = "secret-santa"
  const val GROUPS = "groups"
  const val SYSTEM_UID_BY_MAIL = "system--uid-by-mail"
}

private object Subcollections {
  const val CATEGORIES = "wishlist-categories"
  const val WISHLIST_ITEMS = "items"
  const val SHARED_WISHLIST_ITEMS = "items"
  const val SHARED_WISHLIST_CHAT_MESSAGES = "chat"
  const val SECRET_SANTA_ASSIGNMENTS = "assignments"
  const val SECRET_SANTA_PARTICIPANTS_WISHLIST = "participants-wishlist"
  const val SECRET_SANTA_PARTICIPANTS_WISHLIST_ITEMS = "items"
}