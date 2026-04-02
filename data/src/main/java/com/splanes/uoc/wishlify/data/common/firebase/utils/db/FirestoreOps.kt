package com.splanes.uoc.wishlify.data.common.firebase.utils.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.tasks.await

inline fun <reified T> QuerySnapshot.readAll(): List<T> =
  documents.mapNotNull { doc -> doc.toObject(T::class.java) }

suspend fun FirebaseFirestore.withBatch(block: suspend (batch: WriteBatch) -> Unit) {
  val batch = batch()
  block(batch)
  batch
    .commit()
    .await()
}