package com.splanes.uoc.wishlify.domain.common.media.repository

import android.net.Uri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath

/**
 * Repository contract for storing and deleting image assets referenced by the domain.
 */
interface ImageMediaRepository {
  /**
   * Uploads an image from the given device [uri] to the logical [path].
   *
   * @return A [Result] containing the remote image URL when the upload succeeds.
   */
  suspend fun upload(path: ImageMediaPath, uri: Uri): Result<String>

  /**
   * Deletes the image associated with the given logical [path].
   */
  suspend fun delete(path: ImageMediaPath): Result<Unit>
}
