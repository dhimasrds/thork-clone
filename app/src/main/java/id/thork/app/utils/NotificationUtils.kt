/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */

package id.thork.app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import id.thork.app.R
import id.thork.app.helper.NotificationData
import org.json.JSONObject
import timber.log.Timber

@SuppressWarnings("deprecation")
class NotificationUtils constructor(val context: Context) {
    private val TAG = NotificationUtils::class.java.name

    private val NOTIFICATION_CHANNEL_ID = 9999
    private var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private val channelId = "id.thork.app"

    init {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun sendNotification() {
        Timber.tag(TAG).i("sendNotification()")
    }

    fun sendNotification(notificationData: NotificationData) {
        val data = JSONObject(notificationData.data)
        Timber.tag(TAG).i("sendNotification() receive data: %s", data)

        val title = data.getString("title")
        val content = data.getString("content")
        if (!title.isNullOrEmpty() && !content.isNullOrEmpty()) {
            val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = NotificationChannel(
                    channelId,
                    "Description here",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.lightColor = Color.GREEN
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_tnb_icon)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.ic_tnb_icon
                        )
                    )
                    .setContentTitle(title)
                    .setContentText(content)
            } else {
                builder = Notification.Builder(context)
                    .setSound(defaultSound)
                    .setSmallIcon(R.drawable.ic_tnb_icon)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.ic_tnb_icon
                        )
                    )
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(Notification.PRIORITY_MAX)
            }
            notificationManager.notify(NOTIFICATION_CHANNEL_ID, builder.build())

//        val notificationBuilder: NotificationCompat.Builder =
//            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_tnb_icon)
//                .setSound(defaultSound)
//                .setContentTitle("Title here")
//                .setContentText("Content here")
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//
//        NotificationManagerCompat.from(context).apply {
//            this.notify(0, notificationBuilder)
//        }
        }


    }
}