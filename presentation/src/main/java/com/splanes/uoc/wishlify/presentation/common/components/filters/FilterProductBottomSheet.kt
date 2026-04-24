package com.splanes.uoc.wishlify.presentation.common.components.filters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EuroSymbol
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.domain.feature.wishlists.model.WishlistItem
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.components.button.IconButtonCustom
import com.splanes.uoc.wishlify.presentation.common.components.input.dropdown.DropdownInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInput
import com.splanes.uoc.wishlify.presentation.common.components.input.text.TextInputState
import com.splanes.uoc.wishlify.presentation.feature.shared.model.SharedWishlistState
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.formatPrice
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.icon
import com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.utils.name
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilterProductBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  filters: List<FilterProduct.Filter>,
  modifier: Modifier = Modifier,
  current: List<FilterProduct> = emptyList(),
  onDismiss: () -> Unit,
  onApply: (List<FilterProduct>) -> Unit
) {
  if (visible && filters.isNotEmpty()) {

    val currentPriceFilters =
      remember(current) { current.filterIsInstance<FilterProduct.Price>() }
    val currentPriorityFilters =
      remember(current) { current.filterIsInstance<FilterProduct.Priority>() }
    val currentProductStateFilters =
      remember(current) { current.filterIsInstance<FilterProduct.ProductState>() }


    val priceStates = remember(currentPriceFilters) {
      val elements = currentPriceFilters.map { filter ->
        val operand = filter.operand()
        val state =
          TextInputState(initialValue = filter.value.value.formatPrice(includeCurrency = false))
        operand to state
      }

      mutableStateMapOf(*elements.toTypedArray())
    }

    var priorityState by remember(currentPriorityFilters) {
      val state = currentPriorityFilters
        .singleOrNull()
        ?.let { filter ->
          filter.operand() to filter.value.value
        }
      mutableStateOf(state)
    }

    val productStateState = remember(currentProductStateFilters) {
      val elements = currentProductStateFilters.map { filter ->
        filter.operand() to filter.value.value
      }
      mutableStateListOf(*elements.toTypedArray())
    }

    val isButtonEnabled by remember(current) {
      derivedStateOf {
        currentPriceFilters != priceStates.toFilters() ||
            currentPriorityFilters != priorityState.toFilters() ||
            currentProductStateFilters != productStateState.toFilters()
      }
    }

    ModalBottomSheet(
      modifier = modifier,
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = stringResource(R.string.filters),
          style = WishlifyTheme.typography.titleLarge,
          color = WishlifyTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filters.contains(FilterProduct.Filter.Price)) {
          PriceFilter(
            states = priceStates,
            onChangeCondition = { old, new ->
              val value = priceStates.remove(old)
              value?.let { priceStates[new] = it }
            },
            onAddCondition = { operand -> priceStates[operand] = TextInputState() },
            onRemoveCondition = { operand -> priceStates.remove(operand) }
          )

          Spacer(modifier = Modifier.height(16.dp))

          HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = WishlifyTheme.colorScheme.outline.copy(alpha = .33f)
          )

          Spacer(modifier = Modifier.height(16.dp))
        }

        if (filters.contains(FilterProduct.Filter.Priority)) {
          PriorityFilter(
            state = priorityState,
            onChange = { priorityState = it },
          )

          Spacer(modifier = Modifier.height(16.dp))

          HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = WishlifyTheme.colorScheme.outline.copy(alpha = .33f)
          )

          Spacer(modifier = Modifier.height(16.dp))
        }

        if (filters.contains(FilterProduct.Filter.ProductState)) {
          ProductStateFilter(
            states = productStateState,
            onChange = {
              productStateState.clear()
              productStateState.addAll(it)
            }
          )

          Spacer(modifier = Modifier.height(16.dp))

          HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = WishlifyTheme.colorScheme.outline.copy(alpha = .33f)
          )

          Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          enabled = isButtonEnabled,
          onClick = {
            onApply(
              priceStates.toFilters() + priorityState.toFilters() + productStateState.toFilters()
            )
          }
        ) {
          ButtonText(text = stringResource(R.string.apply_filter))
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PriceFilter(
  states: Map<Operand, TextInputState>,
  onChangeCondition: (old: Operand, new: Operand) -> Unit,
  onAddCondition: (Operand) -> Unit,
  onRemoveCondition: (Operand) -> Unit,
) {

  val operandOptions = Operand.entries.map { operand ->
    DropdownInput.Option(
      id = operand.id,
      text = operand.text(),
      leadingIcon = operand.icon(),
    )
  }

  val isMoreButtonEnabled by remember {
    derivedStateOf {
      val operands = states.keys

      operands.isEmpty() ||
          (Operand.EqualTo !in operands && operands.count() == 1)
    }
  }

  Text(
    text = stringResource(R.string.filter_product_price),
    style = WishlifyTheme.typography.titleMedium,
    color = WishlifyTheme.colorScheme.onSurface
  )

  Spacer(Modifier.height(16.dp))

  states.keys.forEachIndexed { index, operand ->
    val inputState = states[operand] ?: return
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      DropdownInput(
        modifier = Modifier.weight(1f),
        items = operandOptions,
        initial = operandOptions.find { it.id == operand.id },
        label = "",
        allowUnselect = false,
        onSelectionChanged = { id ->
          if (id != null) {
            val new = Operand.findById(id)
            onChangeCondition(operand, new)
          }
        }
      )

      TextInput(
        modifier = Modifier.weight(1f),
        state = inputState,
        leadingIcon = Icons.Rounded.EuroSymbol,
        label = stringResource(R.string.wishlists_new_item_price_input),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
      )

      IconButtonCustom(
        imageVector = Icons.Rounded.Close,
        contentSize = DpSize(24.dp, 24.dp),
        onClick = { onRemoveCondition(operand) }
      )
    }

    if (index != states.keys.indices.last) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(end = 36.dp),
        text = "i",
        style = WishlifyTheme.typography.labelMedium,
        color = WishlifyTheme.colorScheme.outline,
        textAlign = TextAlign.Center
      )
    }
  }

  OutlinedButton(
    modifier = Modifier.padding(top = 8.dp),
    shapes = ButtonShape,
    enabled = isMoreButtonEnabled,
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = WishlifyTheme.colorScheme.primary
    ),
    border = BorderStroke(
      width = 1.dp,
      color = if (isMoreButtonEnabled) {
        WishlifyTheme.colorScheme.primary
      } else {
        WishlifyTheme.colorScheme.outline.copy(alpha = .33f)
      }
    ),
    onClick = {
      when {
        states.keys.isEmpty() -> Operand.EqualTo
        states.keys.count() == 1 && states.keys.single() == Operand.LessThan -> Operand.GreaterThan
        states.keys.count() == 1 && states.keys.single() == Operand.GreaterThan -> Operand.LessThan
        else -> null /* Should not happen.. I guess xD */
      }?.run(onAddCondition)
    }
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Rounded.Add,
        contentDescription = null
      )
      ButtonText(
        modifier = Modifier.padding(horizontal = 8.dp),
        text = stringResource(R.string.add),
        style = WishlifyTheme.typography.labelLarge
      )
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PriorityFilter(
  state: Pair<Operand, WishlistItem.Priority>?,
  onChange: (Pair<Operand, WishlistItem.Priority>?) -> Unit,
) {
  val operandOptions = Operand.entries.map { operand ->
    DropdownInput.Option(
      id = operand.id,
      text = operand.text(),
      leadingIcon = operand.icon(),
    )
  }

  val priorityOptions = WishlistItem.Priority.entries.mapIndexed { index, prio ->
    DropdownInput.Option(
      id = index,
      text = prio.name(),
      leadingIcon = rememberVectorPainter(prio.icon()),
    )
  }

  val isAddMoreVisible by remember(state) { derivedStateOf { state == null } }

  Text(
    text = stringResource(R.string.filter_product_priority),
    style = WishlifyTheme.typography.titleMedium,
    color = WishlifyTheme.colorScheme.onSurface
  )

  Spacer(Modifier.height(16.dp))

  state?.let { (operand, priority) ->
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      DropdownInput(
        modifier = Modifier.weight(1f),
        items = operandOptions,
        initial = operandOptions.find { it.id == operand.id },
        label = "",
        allowUnselect = false,
        onSelectionChanged = { id ->
          if (id != null) {
            val new = Operand.findById(id)
            onChange(new to priority)
          }
        }
      )

      DropdownInput(
        modifier = Modifier.weight(1f),
        items = priorityOptions,
        initial = priorityOptions.find { WishlistItem.Priority.entries[it.id] == priority },
        label = "",
        allowUnselect = false,
        onSelectionChanged = { id ->
          if (id != null) {
            val new = WishlistItem.Priority.entries[id]
            onChange(operand to new)
          }
        }
      )

      IconButtonCustom(
        imageVector = Icons.Rounded.Close,
        contentSize = DpSize(24.dp, 24.dp),
        onClick = { onChange(null) }
      )
    }
  }

  if (isAddMoreVisible) {
    OutlinedButton(
      modifier = Modifier.padding(top = 8.dp),
      shapes = ButtonShape,
      colors = ButtonDefaults.outlinedButtonColors(
        contentColor = WishlifyTheme.colorScheme.primary
      ),
      border = BorderStroke(width = 1.dp, color = WishlifyTheme.colorScheme.primary),
      onClick = { onChange(Operand.EqualTo to WishlistItem.Priority.Standard) }
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Rounded.Add,
          contentDescription = null
        )
        ButtonText(
          modifier = Modifier.padding(horizontal = 8.dp),
          text = stringResource(R.string.add),
          style = WishlifyTheme.typography.labelLarge
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProductStateFilter(
  states: List<Pair<Operand, SharedWishlistState>>,
  onChange: (List<Pair<Operand, SharedWishlistState>>) -> Unit,
) {

  val operandOption = DropdownInput.Option(
    id = 0,
    text = Operand.EqualTo.text(),
    leadingIcon = Operand.EqualTo.icon(),
  )

  val statesOptions = SharedWishlistState.entries
    .filter { s -> s !in states.map { it.second } }
    .map { s ->
      DropdownInput.Option(
        id = s.hashCode(),
        text = s.text(),
        leadingIcon = s.icon(),
      )
    }

  val isAddMoreButtonEnabled by remember {
    derivedStateOf {
      states.isEmpty() || states.map { it.second }.count() < SharedWishlistState.entries.count()
    }
  }

  Text(
    text = stringResource(R.string.filter_product_price),
    style = WishlifyTheme.typography.titleMedium,
    color = WishlifyTheme.colorScheme.onSurface
  )

  Spacer(Modifier.height(16.dp))

  states.forEachIndexed { index, (_, state) ->
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      DropdownInput(
        modifier = Modifier.weight(1f),
        items = listOf(operandOption),
        initial = operandOption,
        label = "",
        readOnly = true,
        allowUnselect = false,
        onSelectionChanged = { }
      )

      DropdownInput(
        modifier = Modifier.weight(1f),
        items = statesOptions,
        initial = statesOptions.find { it.id == state.hashCode() },
        label = "",
        allowUnselect = false,
        onSelectionChanged = { id ->
          if (id != null) {
            val newState = SharedWishlistState.entries.first { it.hashCode() == id }
            val updated = states.filter { (_, s) -> s == state } + (Operand.EqualTo to newState)
            onChange(updated)
          }
        }
      )

      IconButtonCustom(
        imageVector = Icons.Rounded.Close,
        contentSize = DpSize(24.dp, 24.dp),
        onClick = {
          val updated = states.filter { (_, s) -> s == state }
          onChange(updated)
        }
      )
    }

    if (index != states.lastIndex) {
      Text(
        modifier = Modifier.fillMaxWidth().padding(end = 36.dp),
        text = "o",
        style = WishlifyTheme.typography.labelMedium,
        color = WishlifyTheme.colorScheme.outline,
        textAlign = TextAlign.Center
      )
    }
  }

  OutlinedButton(
    modifier = Modifier.padding(top = 8.dp),
    shapes = ButtonShape,
    enabled = isAddMoreButtonEnabled,
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = WishlifyTheme.colorScheme.primary
    ),
    border = BorderStroke(
      width = 1.dp,
      color = if (isAddMoreButtonEnabled) {
        WishlifyTheme.colorScheme.primary
      } else {
        WishlifyTheme.colorScheme.outline.copy(alpha = .33f)
      }
    ),
    onClick = {
      val nextAvailableState = SharedWishlistState.entries.first {
        it !in states.map { s -> s.second }
      }
      val updated = states + (Operand.EqualTo to nextAvailableState)
      onChange(updated)
    }
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Rounded.Add,
        contentDescription = null
      )
      ButtonText(
        modifier = Modifier.padding(horizontal = 8.dp),
        text = stringResource(R.string.add),
        style = WishlifyTheme.typography.labelLarge
      )
    }
  }
}


sealed interface FilterProduct {

  data class Price(val value: Value<Float>) : FilterProduct

  data class Priority(val value: Value<WishlistItem.Priority>) : FilterProduct

  data class ProductState(val value: Value<SharedWishlistState>) : FilterProduct

  sealed interface Value<out T> {
    val value: T
  }

  data class LessThan<T>(override val value: T) : Value<T>
  data class GreaterThan<T>(override val value: T) : Value<T>
  data class EqualTo<T>(override val value: T) : Value<T>

  enum class Filter {
    Price,
    Priority,
    ProductState,
  }
}

private enum class Operand(val id: Int) {
  LessThan(id = -1),
  GreaterThan(id = 1),
  EqualTo(id = 0)
  ;

  @Composable
  fun text() = when (this) {
    LessThan -> stringResource(R.string.filter_operand_less_than)
    GreaterThan -> stringResource(R.string.filter_operand_greater_than)
    EqualTo -> stringResource(R.string.filter_operand_equal_to)
  }

  @Composable
  fun icon() = when (this) {
    LessThan -> rememberVectorPainter(Icons.Rounded.ChevronLeft)
    GreaterThan -> rememberVectorPainter(Icons.Rounded.ChevronRight)
    EqualTo -> painterResource(R.drawable.ic_equal)
  }

  companion object {
    fun findById(id: Int) = entries.first { it.id == id }
  }
}

private fun Map<Operand, TextInputState>.toFilters(): List<FilterProduct> =
  this
    .filter { (_, input) -> input.text.isNotBlank() && input.text.toFloatOrNull() != null }
    .mapValues { (_, input) -> input.text.toFloat() }
    .map { (op, price) ->
      val value = when (op) {
        Operand.LessThan -> FilterProduct.LessThan(price)
        Operand.GreaterThan -> FilterProduct.GreaterThan(price)
        Operand.EqualTo -> FilterProduct.EqualTo(price)
      }
      FilterProduct.Price(value)
    }

private fun Pair<Operand, WishlistItem.Priority>?.toFilters(): List<FilterProduct> = buildList {
  if (this@toFilters != null) {
    val (op, priority) = this@toFilters
    val value = when (op) {
      Operand.LessThan -> FilterProduct.LessThan(priority)
      Operand.GreaterThan -> FilterProduct.GreaterThan(priority)
      Operand.EqualTo -> FilterProduct.EqualTo(priority)
    }
    add(FilterProduct.Priority(value))
  }
}

private fun List<Pair<Operand, SharedWishlistState>>.toFilters(): List<FilterProduct> =
  map { (op, state) ->
    val value = when (op) {
      Operand.LessThan -> FilterProduct.LessThan(state)
      Operand.GreaterThan -> FilterProduct.GreaterThan(state)
      Operand.EqualTo -> FilterProduct.EqualTo(state)
    }
    FilterProduct.ProductState(value)
  }

private fun FilterProduct.operand(): Operand {
  val value = when (this) {
    is FilterProduct.Price -> value
    is FilterProduct.Priority -> value
    is FilterProduct.ProductState -> value
  }
  return when (value) {
    is FilterProduct.EqualTo<*> -> Operand.EqualTo
    is FilterProduct.GreaterThan<*> -> Operand.GreaterThan
    is FilterProduct.LessThan<*> -> Operand.LessThan
  }
}

@Composable
private fun SharedWishlistState.text() = when (this) {
  SharedWishlistState.Purchase -> R.string.shared_wishlists_item_state_purchased
  SharedWishlistState.Lock -> R.string.shared_wishlists_item_state_lock
  SharedWishlistState.RequestShare -> R.string.shared_wishlists_item_state_share_request
  SharedWishlistState.Available -> R.string.shared_wishlists_item_state_available
}.let { id -> stringResource(id) }

@Composable
private fun SharedWishlistState.icon() = when (this) {
  SharedWishlistState.Purchase -> rememberVectorPainter(Icons.Outlined.Verified)
  SharedWishlistState.Lock -> rememberVectorPainter(Icons.Outlined.LockClock)
  SharedWishlistState.RequestShare -> painterResource(R.drawable.ic_share_request)
  SharedWishlistState.Available -> rememberVectorPainter(Icons.Outlined.LocalGroceryStore)
}