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
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.RemoteMessage
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import id.thork.app.messaging.ThorFcmService
import io.objectbox.android.ObjectBoxLiveData
import timber.log.Timber
import javax.inject.Inject

@Module
@InstallIn(ActivityRetainedComponent::class)
class PushNotificationLiveData @Inject constructor(
    val context: Context) : LiveData<String>() {
    private val TAG = PushNotificationLiveData::class.java.name

    override fun onActive() {
        super.onActive()
        Timber.tag(TAG).i("onActive() %s", true)
        postValue("Hello World x1x")


//        for (i in 1 .. 100) {
//            Thread.sleep(1000)
//            postValue("Hello World x1x $i")
//        }
    }

    override fun setValue(value: String?) {
        super.setValue(value)
    }

    override fun postValue(value: String?) {
        super.postValue(value)
        Timber.tag(TAG).i("postValue() x1x %s", value)
    }

    fun onMessageReceived(remoteMessage: RemoteMessage) {
        val message:String = remoteMessage.data.toString()
        Timber.tag(TAG).i("onMessageReceived() x1x %s", message)
        postValue(message)
    }
}
