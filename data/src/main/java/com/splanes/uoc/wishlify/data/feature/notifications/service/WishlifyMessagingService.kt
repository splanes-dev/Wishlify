package com.splanes.uoc.wishlify.data.feature.notifications.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.splanes.uoc.wishlify.data.feature.notifications.mapper.PushNotificationDataMapper
import com.splanes.uoc.wishlify.domain.feature.notifications.PushNotificationHandler
import com.splanes.uoc.wishlify.domain.feature.session.repository.SessionRepository
import com.splanes.uoc.wishlify.domain.feature.user.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class WishlifyMessagingService : FirebaseMessagingService(), KoinComponent {

  private val scope = CoroutineScope(Dispatchers.Default)
  private val sessionRepository: SessionRepository by inject()
  private val userRepository: UserRepository by inject()

  private val handler: PushNotificationHandler by inject()
  private val mapper: PushNotificationDataMapper by inject()

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    Timber.tag("PushNotifications").d("onNewToken: $token")
    scope.launch {
      userRepository.storeUserToken(token)
      val uid = sessionRepository.getCurrentUid().getOrNull()
      if (uid != null) {
        userRepository.updateUserToken(uid)
      }
    }
  }

  override fun onMessageReceived(message: RemoteMessage) {
    super.onMessageReceived(message)
    Timber.tag("PushNotifications").d("onMessageReceived: ${message.data}")
    val push = mapper.map(message)
    scope.launch { handler.handle(push) }
  }
}