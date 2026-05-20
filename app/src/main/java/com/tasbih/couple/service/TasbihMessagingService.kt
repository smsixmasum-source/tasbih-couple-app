package com.tasbih.couple.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TasbihMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {}
    override fun onNewToken(token: String) {}
}
