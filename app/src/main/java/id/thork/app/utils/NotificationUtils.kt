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
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.helper.NotificationData
import id.thork.app.pages.detail_wo.DetailWoActivity
import org.json.JSONObject
import timber.log.Timber

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

    @Suppress("DEPRECATION")
    fun sendNotification(notificationData: NotificationData) {
        val data = JSONObject(notificationData.data)
        Timber.tag(TAG).i("sendNotification() receive data: %s", data)

        val title = data.getString("title")
        val content = data.getString("content")
        val wonum = data.getString("wonum")
        Timber.tag(TAG).i("sendNotification() receive wonum: %s", wonum)

        val intent = Intent(context, DetailWoActivity::class.java)
        intent.putExtra(BaseParam.APP_WONUM, wonum)

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

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
                    .setSmallIcon(R.drawable.ic_logo_this_white)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.ic_logo_this_white
                        )
                    )
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
            } else {
                builder = Notification.Builder(context)
                    .setSound(defaultSound)
                    .setSmallIcon(R.drawable.ic_logo_this_white)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.ic_logo_this_white
                        )
                    )
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
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