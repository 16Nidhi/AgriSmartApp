package com.example.agrismart.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver to handle scheduled weather alerts.
 * Syllabus: Unit II (Managing Application Communication)
 */
class WeatherReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        val title = intent.getStringExtra("title") ?: "Weather Update"
        val message = intent.getStringExtra("message") ?: "Check your crops today!"
        
        notificationHelper.showBasicNotification(title, message)
    }
}
