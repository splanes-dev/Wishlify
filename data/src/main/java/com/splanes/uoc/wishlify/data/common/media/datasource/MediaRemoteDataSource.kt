package com.splanes.uoc.wishlify.data.common.media.datasource

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import com.splanes.uoc.wishlify.domain.common.error.GenericError
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.UnknownHostException

/**
 * Firebase Storage-backed data source for uploading and deleting media files.
 *
 * It translates low-level storage errors into domain-facing generic errors.
 */
class MediaRemoteDataSource(
  private val context: Context,
  private val storage: FirebaseStorage
) {

  /** Uploads the local file referenced by [uri] to the storage [path]. */
  suspend fun upload(path: String, uri: Uri): String {
    try {
      val ref = storage
        .reference
        .child(path)

      val metadata = storageMetadata {
        contentType = context.contentResolver.getType(uri)
      }

      // Upload
      ref.putFile(uri, metadata).await()
      // Download url
      val url = ref.downloadUrl.await().toString()

      return url
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }

  /** Deletes the remote file stored at [path]. */
  suspend fun delete(path: String) {
    try {
      storage
        .reference
        .child(path)
        .delete()
        .await()
    } catch (_: UnknownHostException) {
      throw GenericError.NoInternet()
    } catch (e: Throwable) {
      Timber.e(e)
      throw GenericError.Unknown(cause = e)
    }
  }
}
