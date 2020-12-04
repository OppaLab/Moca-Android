package com.oppalab.moca.util

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.oppalab.moca.PostDetailActivity
import com.oppalab.moca.R


class MocaFirebaseMessagingService: FirebaseMessagingService() {
    val currentUser = PreferenceManager.getLong(applicationContext, "userId")

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationTitle = remoteMessage.notification!!.title
        val notificationBody = remoteMessage.notification!!.body
        val postId = remoteMessage.data["postId"]

        val resultIntent = Intent(this, PostDetailActivity::class.java)
        resultIntent.putExtra("postId", postId)

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationBuilder = NotificationCompat.Builder(this).apply {
            setSmallIcon(R.drawable.moca)
            setContentTitle(notificationTitle)
            setContentText(notificationBody)
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            setContentIntent(pendingIntent)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())

    }
}