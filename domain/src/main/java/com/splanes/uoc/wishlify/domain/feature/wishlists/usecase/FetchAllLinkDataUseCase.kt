package com.splanes.uoc.wishlify.domain.feature.wishlists.usecase

import com.splanes.uoc.wishlify.domain.common.usecase.UseCase
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItemUrlData
import com.splanes.uoc.wishlify.domain.feature.wishlists.repository.WishlistsRepository

class FetchAllLinkDataUseCase(
  private val repository: WishlistsRepository
) : UseCase() {

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

  private fun WishlistItemUrlData.isEnough() =
    imageUrl != null && (product != null || price != null)
}