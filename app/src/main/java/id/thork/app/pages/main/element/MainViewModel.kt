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

package id.thork.app.pages.main.element

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.workmanager.WorkerCoordinator

class MainViewModel @ViewModelInject constructor(
    private val context: Context,
    private val workerCoordinator: WorkerCoordinator,
    private val appSession: AppSession
    ) : LiveCoroutinesViewModel() {
    val TAG = MainViewModel::class.java.name

    fun checkWorkManager() {
        workerCoordinator.addSyncWoQueue()
    }

    fun reInitAppSession(){
        appSession.reinitUser()
    }
}