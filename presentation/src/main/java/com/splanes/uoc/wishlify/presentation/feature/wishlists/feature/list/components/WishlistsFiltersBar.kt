package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.FiltersBar
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.WishlistsFiltersState
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsFilter

@Composable
fun WishlistsFiltersBar(
  filtersState: WishlistsFiltersState,
  modifier: Modifier = Modifier,
  onOpenFilter: (WishlistsFilter) -> Unit,
  onUpdateState: (WishlistsFiltersState) -> Unit,
) {
  FiltersBar(
    modifier = modifier,
    filters = filtersState.toFilters(),
    onFilterClick = { filter -> onOpenFilter(filter) },
    onFilterClear = { filter ->
      when (filter) {
        is WishlistsFilter.Target -> filtersState.copy(target = null)
        is WishlistsFilter.Category -> filtersState.copy(category = null)
        is WishlistsFilter.ShareStatus -> filtersState.copy(shareStatus = null)
        is WishlistsFilter.Availability -> filtersState.copy(availability = null)
      }.let { state -> onUpdateState(state) }
    }
  )
}

@Composable
private fun WishlistsFiltersState.toFilters(): List<FiltersBar.Filter<WishlistsFilter>> =
  listOf(
    WishlistFilterTarget.copy(
      item = target ?: WishlistsFilter.TargetUnselected,
      text = target.text(),
      selected = target != null
    ),
    WishlistFilterCategory.copy(
      item = category ?: WishlistsFilter.CategoryUnselected,
      text = category.text(),
      selected = category != null
    ),
    WishlistFilterShareStatus.copy(
      item = shareStatus ?: WishlistsFilter.ShareStatusUnselected,
      text = shareStatus.text(),
      selected = shareStatus != null,
    ),
    WishlistFilterAvailability.copy(
      item = availability ?: WishlistsFilter.AvailabilityUnselected,
      text = availability.text(),
      selected = availability != null
    )
  )

private val WishlistFilterTarget
  @Composable
  get(): FiltersBar.Filter<WishlistsFilter> = FiltersBar.filterOf(
    item = WishlistsFilter.TargetUnselected,
    leadingIcon = Icons.Outlined.Person,
    text = stringResource(R.string.wishlists_filters_target_unselected),
    selected = false
  )

private val WishlistFilterCategory
  @Composable
  get(): FiltersBar.Filter<WishlistsFilter> = FiltersBar.filterOf(
    item = WishlistsFilter.CategoryUnselected,
    leadingIcon = Icons.Outlined.Sell,
    text = stringResource(R.string.wishlists_filters_category_unselected),
    selected = false
  )

private val WishlistFilterShareStatus
  @Composable
  get(): FiltersBar.Filter<WishlistsFilter> = FiltersBar.filterOf(
    item = WishlistsFilter.ShareStatusUnselected,
    leadingIcon = Icons.Outlined.Share,
    text = stringResource(R.string.wishlists_filters_share_status_unselected),
    selected = false
  )

private val WishlistFilterAvailability
  @Composable
  get(): FiltersBar.Filter<WishlistsFilter> = FiltersBar.Filter(
    item = WishlistsFilter.AvailabilityUnselected,
    leadingIcon = painterResource(R.drawable.ic_gift),
    text = stringResource(R.string.wishlists_filters_availability_unselected),
    selected = false
  )

@Composable
private fun WishlistsFilter.Target?.text() = when (this) {
  WishlistsFilter.Own -> stringResource(R.string.wishlists_filters_target_own)
  WishlistsFilter.ThirdParty -> stringResource(R.string.wishlists_filters_target_third_party)
  else -> stringResource(R.string.wishlists_filters_target_unselected)
}

@Composable
private fun WishlistsFilter.Category?.text() = when (this) {
  is WishlistsFilter.Categories -> stringResource(R.string.wishlists_filters_category_selected, values.count())
  else -> stringResource(R.string.wishlists_filters_category_unselected)
}

@Composable
private fun WishlistsFilter.ShareStatus?.text() = when (this) {
  WishlistsFilter.NotShared -> stringResource(R.string.wishlists_filters_share_status_private)
  WishlistsFilter.OnSecretSantaEvent -> stringResource(R.string.wishlists_filters_share_status_secret_santa)
  WishlistsFilter.OnSharedWishlist -> stringResource(R.string.wishlists_filters_share_status_shared_wishlists)
  else -> stringResource(R.string.wishlists_filters_share_status_unselected)
}

@Composable
private fun WishlistsFilter.Availability?.text() = when (this) {
  WishlistsFilter.ItemsAvailable -> stringResource(R.string.wishlists_filters_availability_with_items)
  WishlistsFilter.ItemsNotAvailable -> stringResource(R.string.wishlists_filters_availability_without_items)
  else -> stringResource(R.string.wishlists_filters_availability_unselected)
}