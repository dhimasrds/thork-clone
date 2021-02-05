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

package id.thork.app.di.module

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.firebase.messaging.RemoteMessage
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class PushNotificationLiveData @Inject constructor(
    val context: Context
) : LiveData<String>() {
    private val TAG = PushNotificationLiveData::class.java.name

    override fun onActive() {
        super.onActive()
    }

    fun onMessageReceived(remoteMessage: RemoteMessage) {
        val message:String = remoteMessage.data.toString()
        postValue(message)
    }
}
