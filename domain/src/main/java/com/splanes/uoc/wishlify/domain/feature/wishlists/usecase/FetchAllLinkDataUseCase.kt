package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItemUrlData
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

/**
 * Extracts product metadata from an item URL.
 *
 * It first uses the primary extraction strategy and falls back to a local one
 * when the result is still incomplete.
 */
class FetchAllLinkDataUseCase(
  private val repository: WishlistsRepository
) : UseCase() {

  /** Fetches the best available metadata for [url]. */
  suspend operator fun invoke(url: String) = execute(NoTimeout) {
    repository.extractUrlData(url)
      .mapCatching { data ->
        if (data.isEnough()) {
          data
        } else {
          repository.extractUrlDataLocally(data, url).getOrThrow()
        }
      }
  }

  /** Returns whether the extracted data is complete enough to skip the local fallback. */
  private fun WishlistItemUrlData.isEnough() =
    imageUrl != null && (product != null || price != null)
}
