package com.splanes.uoc.wishlify.presentation.common.components.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EuroSymbol
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonCustom
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistState
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.formatPrice
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.name
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@Composable
fun FilterProductBar(
  filters: List<FilterProduct>,
  modifier: Modifier = Modifier,
  onChange: (List<FilterProduct>) -> Unit,
  onOpenFilters: () -> Unit,
) {
  LazyRow(
    modifier = modifier.background(color = WishlifyTheme.colorScheme.surface),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    items(filters) { filter ->
      FilterProductBarChip(
        filter = filter,
        onClick = onOpenFilters,
        onRemove = { onChange(filters - filter) }
      )
    }
  }
}

@Composable
fun FilterProductBarChip(
  filter: FilterProduct,
  onClick: () -> Unit,
  onRemove: () -> Unit,
) {

  FilterChip(
    selected = true,
    label = { Text(text = filter.text()) },
    leadingIcon = {
      Icon(
        modifier = Modifier.size(20.dp),
        painter = filter.icon(),
        contentDescription = filter.text(),
        tint = WishlifyTheme.colorScheme.primary
      )
    },
    trailingIcon = {
      IconButtonCustom(
        imageVector = Icons.Rounded.Close,
        contentSize = DpSize(24.dp, 24.dp),
        onClick = onRemove
      )
    },
    border = FilterChipDefaults.filterChipBorder(
      enabled = true,
      selected = true,
      selectedBorderWidth = 1.dp,
      selectedBorderColor = WishlifyTheme.colorScheme.primary
    ),
    onClick = onClick
  )
}

@Composable
private fun FilterProduct.text() = when (this) {
  is FilterProduct.Price -> when (value) {
    is FilterProduct.EqualTo<*> -> "= ${value.value.formatPrice(includeCurrency = false)}"
    is FilterProduct.GreaterThan<*> -> "> ${value.value.formatPrice(includeCurrency = false)}"
    is FilterProduct.LessThan<*> -> "< ${value.value.formatPrice(includeCurrency = false)}"
  }
  is FilterProduct.Priority -> when (value) {
    is FilterProduct.EqualTo<*> -> "= ${value.value.name()}"
    is FilterProduct.GreaterThan<*> -> "> ${value.value.name()}"
    is FilterProduct.LessThan<*> -> "< ${value.value.name()}"
  }
  is FilterProduct.ProductState -> when (value) {
    is FilterProduct.EqualTo<*> -> "= ${value.value.name()}"
    else -> error("Filter product operand not allowed for ProductState $value")
  }
}

@Composable
private fun FilterProduct.icon() = when (this) {
  is FilterProduct.Price -> rememberVectorPainter(Icons.Rounded.EuroSymbol)
  is FilterProduct.Priority -> rememberVectorPainter(Icons.Rounded.LocalFireDepartment)
  is FilterProduct.ProductState -> painterResource(R.drawable.ic_gift)
}

@Composable
private fun SharedWishlistState.name() = when (this) {
  SharedWishlistState.Purchase -> R.string.shared_wishlists_item_state_purchased
  SharedWishlistState.Lock -> R.string.shared_wishlists_item_state_lock
  SharedWishlistState.RequestShare -> R.string.shared_wishlists_item_state_share_request
  SharedWishlistState.Available -> R.string.shared_wishlists_item_state_available
}.let { id -> stringResource(id) }