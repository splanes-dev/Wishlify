package com.splanes.uoc.wishlify.data.common.firebase.utils.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

/** Deserializes all documents of a query snapshot into instances of [T]. */
inline fun <reified T> QuerySnapshot.readAll(): List<T> =
  documents.mapNotNull { doc -> doc.toObject<T>() }

/**
 * Executes a Firestore batch operation and commits it once the provided [block] completes.
 */
suspend fun FirebaseFirestore.withBatch(block: suspend (batch: WriteBatch) -> Unit) {
  val batch = batch()
  block(batch)
  batch
    .commit()
    .await()
}
