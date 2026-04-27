package com.splanes.uoc.wishlify.presentation.feature.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.splanes.uoc.wishlify.domain.feature.notifications.PushNotificationChannel
import com.splanes.uoc.wishlify.domain.feature.notifications.PushNotificationHandler
import com.splanes.uoc.wishlify.domain.feature.notifications.model.PushNotification
import com.splanes.uoc.wishlify.presentation.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Presentation-layer implementation of [PushNotificationHandler] responsible for rendering
 * domain push notifications as Android system notifications.
 */
class PushNotificationHandlerImpl(
  private val context: Context,
  private val pendingIntentFactory: PushNotificationPendingIntentFactory,
) : PushNotificationHandler {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  /**
   * Displays the received push notification when the app has notification permission.
   */
  override fun handle(push: PushNotification) {
    if (context.isPermissionGranted()) {
      show(push)
    }
  }

  /**
   * Builds and posts the system notification, downloading the remote image when available.
   */
  @SuppressLint("MissingPermission")
  private fun show(push: PushNotification) {
    scope.launch {
      val title = HtmlCompat.fromHtml(push.title, FROM_HTML_MODE_LEGACY)
      val body = HtmlCompat.fromHtml(push.body, FROM_HTML_MODE_LEGACY)
      val bitmap = push.imageUrl?.toBitmapOrNull()

      val notification = NotificationCompat.Builder(context, channelIdOf(push))
        .setSmallIcon(R.drawable.ic_gift)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(priorityOf(push))
        .setLargeIcon(bitmap)
        .setStyle(
          if (bitmap != null) {
            NotificationCompat.BigPictureStyle()
              .bigPicture(bitmap)
              .bigLargeIcon(null as Bitmap?)
              .setBigContentTitle(title)
              .setSummaryText(body)
          } else {
            NotificationCompat.BigTextStyle()
              .setBigContentTitle(title)
              .bigText(body)
          }
        )
        .setAutoCancel(true)
        .setContentIntent(pendingIntentFactory.create(push))
        .build()

      NotificationManagerCompat
        .from(context)
        .notify(
          notificationIdOf(push),
          notification
        )
    }
  }

  /**
   * Maps each push type to the notification channel used to publish it.
   */
  private fun channelIdOf(push: PushNotification) =
    when (push) {
      is PushNotification.Chat -> PushNotificationChannel.CHAT
      else -> PushNotificationChannel.GENERAL
    }

  /**
   * Maps each push type to the system priority that best matches its urgency.
   */
  private fun priorityOf(push: PushNotification) =
    when (push) {
      is PushNotification.Chat -> NotificationCompat.PRIORITY_HIGH
      else -> NotificationCompat.PRIORITY_DEFAULT
    }

  /**
   * Generates a stable notification id from the push type and deeplink destination.
   */
  private fun notificationIdOf(push: PushNotification) =
    "${push::class.simpleName}:${push.deeplink}".hashCode()

  /**
   * Checks whether the app can publish notifications on the current Android version.
   */
  private fun Context.isPermissionGranted() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
      ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    else -> true
  }

  /**
   * Downloads the image referenced by this URL and decodes it as a [Bitmap], or returns `null`
   * when the download fails.
   */
  suspend fun String.toBitmapOrNull(): Bitmap? = withContext(Dispatchers.IO) {
    runCatching {
      val connection = URL(this@toBitmapOrNull).openConnection() as HttpURLConnection
      connection.doInput = true
      connection.connectTimeout = 8_000
      connection.readTimeout = 8_000
      connection.connect()
      connection.inputStream.use { input ->
        BitmapFactory.decodeStream(input)
      }
    }.getOrNull()
  }
}
