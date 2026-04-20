package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.Category
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.FilterBottomSheet
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.list.model.WishlistsFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistFilterBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  filter: WishlistsFilter,
  categories: List<Category>,
  onDismiss: () -> Unit,
  onApplyFilters: (WishlistsFilter?) -> Unit,
) {

  val options = filter.options(categories)

  FilterBottomSheet(
    visible = visible,
    sheetState = sheetState,
    title = filter.title(),
    description = filter.description(),
    options = options,
    selected = filter.selected(options),
    allowMultiChoice = filter is WishlistsFilter.Category,
    onApplyFilter = { items ->
      when {
        items.count() == 0 -> onApplyFilters(null)
        items.count() == 1 -> onApplyFilters(items.singleOrNull())
        else -> items
          .filterIsInstance<WishlistsFilter.Categories>()
          .flatMap { it.values }
          .let(WishlistsFilter::Categories)
          .let { onApplyFilters(it) }
      }
    },
    onDismiss = onDismiss
  )
}

@Composable
private fun WishlistsFilter.title() = when (this) {
  is WishlistsFilter.Target -> R.string.wishlists_filters_target_title
  is WishlistsFilter.Category -> R.string.wishlists_filters_category_title
  is WishlistsFilter.ShareStatus -> R.string.wishlists_filters_share_status_title
  is WishlistsFilter.Availability -> R.string.wishlists_filters_availability_title
}.let { id -> stringResource(id) }

@Composable
private fun WishlistsFilter.description() = when (this) {
  is WishlistsFilter.Target -> R.string.wishlists_filters_target_description
  is WishlistsFilter.Category -> R.string.wishlists_filters_category_description
  is WishlistsFilter.ShareStatus -> R.string.wishlists_filters_share_status_description
  is WishlistsFilter.Availability -> R.string.wishlists_filters_availability_description
}.let { id -> stringResource(id) }

@Composable
private fun WishlistsFilter.options(categories: List<Category>): List<FilterBottomSheet.Option<WishlistsFilter>> =
  buildList {
    when (this@options) {
      is WishlistsFilter.Target -> {
        add(
          FilterBottomSheet.Option(
            item = WishlistsFilter.Own,
            text = stringResource(R.string.wishlists_filters_target_own)
          )
        )
        add(
          FilterBottomSheet.Option(
            item = WishlistsFilter.ThirdParty,
            text = stringResource(R.string.wishlists_filters_target_third_party)
          )
        )
      }

      is WishlistsFilter.Category -> {
        val cats: List<FilterBottomSheet.Option<WishlistsFilter>> = categories
          .map { category ->
            FilterBottomSheet.Option(
              item = WishlistsFilter.Categories(listOf(category)),
              text = category.name
            )
          }
        addAll(cats)
      }

      is WishlistsFilter.ShareStatus -> {
        add(
          FilterBottomSheet.Option(
            item = WishlistsFilter.NotShared,
            text = stringResource(R.string.wishlists_filters_share_status_private)
          )
        )
        add(
          FilterBottomSheet.Option(
            item = WishlistsFilter.OnSharedWishlist,
            text = stringResource(R.string.wishlists_filters_share_status_shared_wishlists)
          )
        )
        add(
          FilterBottomSheet.Option(
            item = WishlistsFilter.OnSecretSantaEvent,
            text = stringResource(R.string.wishlists_filters_share_status_secret_santa)
          )
        )
      }

      is WishlistsFilter.Availability -> {
        add(
          FilterBottomSheet.Option(
            item = WishlistsFilter.ItemsAvailable,
            text = stringResource(R.string.wishlists_filters_availability_with_items)
          )
        )
        add(
          FilterBottomSheet.Option(
            item = WishlistsFilter.ItemsNotAvailable,
            text = stringResource(R.string.wishlists_filters_availability_without_items)
          )
        )
      }
    }
  }

@Composable
private fun WishlistsFilter.selected(options: List<FilterBottomSheet.Option<WishlistsFilter>>): List<FilterBottomSheet.Option<WishlistsFilter>> =
  when (this) {
    WishlistsFilter.Own,
    WishlistsFilter.ThirdParty,
    WishlistsFilter.ItemsAvailable,
    WishlistsFilter.ItemsNotAvailable,
    WishlistsFilter.NotShared,
    WishlistsFilter.OnSecretSantaEvent,
    WishlistsFilter.OnSharedWishlist -> options.filter { it.item == this }

    is WishlistsFilter.Categories -> options
      .filter { it.item is WishlistsFilter.Categories && values.contains(it.item.values.singleOrNull()) }

    else -> emptyList()
  }