package com.splanes.uoc.wishlify.domain.common.media.repository

import android.net.Uri
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath

interface ImageMediaRepository {
  suspend fun upload(path: ImageMediaPath, uri: Uri): Result<String>

  suspend fun delete(path: ImageMediaPath): Result<Unit>
}