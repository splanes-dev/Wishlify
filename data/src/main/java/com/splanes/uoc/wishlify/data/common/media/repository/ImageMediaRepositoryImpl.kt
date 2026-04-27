package com.splanes.uoc.wishlify.data.common.media.repository

import android.net.Uri
import com.splanes.uoc.wishlify.data.common.media.datasource.MediaRemoteDataSource
import com.splanes.uoc.wishlify.data.common.media.mapper.ImageMediaDataMapper
import com.splanes.uoc.wishlify.domain.common.media.model.ImageMediaPath
import com.splanes.uoc.wishlify.domain.common.media.repository.ImageMediaRepository

/** Data-layer implementation of [ImageMediaRepository] backed by Firebase Storage. */
class ImageMediaRepositoryImpl(
  private val remoteDataSource: MediaRemoteDataSource,
  private val mapper: ImageMediaDataMapper,
) : ImageMediaRepository {

  /** Uploads a local image to the storage location derived from the domain [path]. */
  override suspend fun upload(path: ImageMediaPath, uri: Uri): Result<String> =
    runCatching {
      val path = mapper.pathOf(path)
      remoteDataSource.upload(path, uri)
    }

  /** Deletes the remote image associated with the domain [path]. */
  override suspend fun delete(path: ImageMediaPath): Result<Unit> =
    runCatching {
      val path = mapper.pathOf(path)
      remoteDataSource.delete(path)
    }
}
