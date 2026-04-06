package com.splanes.uoc.wishlify.presentation.feature.wishlists.feature.detail.share.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistShareInfoBottomSheet(
  visible: Boolean,
  sheetState: SheetState,
  onDismiss: () -> Unit,
) {

  if (visible) {
    ModalBottomSheet(
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

          Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = stringResource(R.string.wishlists_share_visibility_modal_title),
            tint = WishlifyTheme.colorScheme.onSurface
          )

          Text(
            text = stringResource(R.string.wishlists_share_visibility_modal_title),
            style = WishlifyTheme.typography.titleLarge,
            color = WishlifyTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(Modifier.height(16.dp))

        Text(
          modifier = Modifier.fillMaxWidth(),
          text = stringResource(R.string.wishlists_share_visibility_modal_description),
          style = WishlifyTheme.typography.bodyMedium,
          color = WishlifyTheme.colorScheme.onSurface,
          textAlign = TextAlign.Justify
        )

        Spacer(Modifier.height(24.dp))
      }
    }
  }
}