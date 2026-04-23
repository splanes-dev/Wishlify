package com.splanes.uoc.wishlify.presentation.common.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.splanes.uoc.wishlify.presentation.R
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonShape
import com.splanes.uoc.wishlify.presentation.common.components.button.ButtonText
import com.splanes.uoc.wishlify.presentation.common.utils.htmlString
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.launch

@SuppressLint("InlinedApi")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PushPermissionRequestBottomSheet(
  visible: Boolean,
  modifier: Modifier = Modifier,
  title: AnnotatedString = htmlString(R.string.request_notification_permission_modal_title),
  description: AnnotatedString = htmlString(R.string.request_notification_permission_modal_description),
  onDismiss: () -> Unit,
) {

  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { /* Do nothing, don't care about result */ }
  )

  if (visible && shouldRequestPermission(context)) {
    ModalBottomSheet(
      modifier = modifier,
      sheetState = sheetState,
      onDismissRequest = onDismiss
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = title,
          style = WishlifyTheme.typography.titleLarge,
          color = WishlifyTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Image(
          modifier = Modifier.fillMaxWidth(),
          painter = painterResource(R.drawable.img_notification_permissions),
          contentDescription = null
        )

        Spacer(Modifier.height(16.dp))

        Text(
          modifier = Modifier.fillMaxWidth(),
          text = description,
          style = WishlifyTheme.typography.titleSmall,
          color = WishlifyTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Button(
          modifier = Modifier.fillMaxWidth(),
          shapes = ButtonShape,
          onClick = {
            coroutineScope
              .launch { sheetState.hide() }
              .invokeOnCompletion {
                onDismiss()
                launcher.launch(NotificationPermission)
              }
          }
        ) {
          ButtonText(text = stringResource(R.string.allow))
        }

        Spacer(Modifier.height(16.dp))
      }
    }
  }
}

private fun shouldRequestPermission(context: Context): Boolean =
  when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
      ActivityCompat.checkSelfPermission(
        context,
        NotificationPermission
      ) != PackageManager.PERMISSION_GRANTED

    else -> false
  }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private val NotificationPermission = Manifest.permission.POST_NOTIFICATIONS