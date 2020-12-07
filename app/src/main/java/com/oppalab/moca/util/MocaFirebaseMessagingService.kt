package com.oppalab.moca.util

import android.app.NotificationChannel
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
import com.squareup.picasso.Picasso


class MocaFirebaseMessagingService: FirebaseMessagingService() {

    private var NOTIFICATION_CHANNEL_ID : String = "10001"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationTitle = remoteMessage.notification!!.title
        val notificationBody = remoteMessage.notification!!.body
        val postId = remoteMessage.data["postId"]
        val thumbnailImageFilePath = remoteMessage.data["thumbnailFilePath"]

        val resultIntent = Intent(this, PostDetailActivity::class.java)
        resultIntent.putExtra("postId", postId)

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationBuilder = NotificationCompat.Builder(this).apply {
            setSmallIcon(R.mipmap.ic_moca_icon)
            setLargeIcon(Picasso.get()
                .load(RetrofitConnection.URL + "/image/thumbnail/" + thumbnailImageFilePath).get())
            setContentTitle(notificationTitle)
            setContentText(notificationBody)
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            setContentIntent(pendingIntent)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, notificationBuilder.build())

    }
}