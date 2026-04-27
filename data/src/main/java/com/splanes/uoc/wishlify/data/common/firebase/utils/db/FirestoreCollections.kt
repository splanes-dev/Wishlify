package com.splanes.uoc.wishlify.data.common.firebase.utils.db

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/** Root `users` collection reference. */
val FirebaseFirestore.users
  get() = collection(Collections.USERS)

/** Root `wishlists` collection reference. */
val FirebaseFirestore.wishlists
  get() = collection(Collections.WISHLISTS)

/** Root `shared-wishlists` collection reference. */
val FirebaseFirestore.sharedWishlists
  get() = collection(Collections.SHARED_WISHLISTS)

/** Root `secret-santa` collection reference. */
val FirebaseFirestore.secretSanta
  get() = collection(Collections.SECRET_SANTA)

/** Root `groups` collection reference. */
val FirebaseFirestore.groups
  get() = collection(Collections.GROUPS)

/** Root system collection used to index users by email. */
val FirebaseFirestore.systemUidByEmail
  get() = collection(Collections.SYSTEM_UID_BY_MAIL)

/** Wishlist categories subcollection reference. */
val DocumentReference.wishlistCategories
  get() = collection(Subcollections.CATEGORIES)

/** Wishlist items subcollection reference. */
val DocumentReference.wishlistItems
  get() = collection(Subcollections.WISHLIST_ITEMS)

/** Shared wishlist items subcollection reference. */
val DocumentReference.sharedWishlistItems
  get() = collection(Subcollections.SHARED_WISHLIST_ITEMS)

/** Shared wishlist chat messages subcollection reference. */
val DocumentReference.sharedWishlistChatMessages
  get() = collection(Subcollections.SHARED_WISHLIST_CHAT_MESSAGES)

/** Secret Santa assignments subcollection reference. */
val DocumentReference.secretSantaAssignments
  get() = collection(Subcollections.SECRET_SANTA_ASSIGNMENTS)

/** Secret Santa participant wishlist subcollection reference. */
val DocumentReference.secretSantaParticipantsWishlist
  get() = collection(Subcollections.SECRET_SANTA_PARTICIPANTS_WISHLIST)

/** Secret Santa participant wishlist items subcollection reference. */
val DocumentReference.secretSantaParticipantsWishlistItems
  get() = collection(Subcollections.SECRET_SANTA_PARTICIPANTS_WISHLIST_ITEMS)

/** Secret Santa chats subcollection reference. */
val DocumentReference.secretSantaChats
  get() = collection(Subcollections.SECRET_SANTA_CHATS)

/** Secret Santa chat messages subcollection reference. */
val DocumentReference.secretSantaChatMessages
  get() = collection(Subcollections.SECRET_SANTA_CHAT_MESSAGES)


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
  const val SECRET_SANTA_CHATS = "chats"
  const val SECRET_SANTA_CHAT_MESSAGES = "messages"
}
