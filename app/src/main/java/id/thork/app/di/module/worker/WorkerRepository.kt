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

package id.thork.app.di.module.worker

import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.WorkOrderApi
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.repository.WorkOrderRepository
import okhttp3.logging.HttpLoggingInterceptor

class WorkerRepository constructor(
    private val preferenceManager: PreferenceManager,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val woCacheDao: WoCacheDao
) {

    fun buildWorkorderRepository(): WorkOrderRepository {
        val workOrderClient = WorkOrderClient(provideWorkOrderApi())
        return WorkOrderRepository(workOrderClient, woCacheDao)
    }

    private fun provideWorkOrderApi(): WorkOrderApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(WorkOrderApi::class.java)
    }
}