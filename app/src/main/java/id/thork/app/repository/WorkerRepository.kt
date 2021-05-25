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

package id.thork.app.repository

import android.content.Context
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.DoclinksApi
import id.thork.app.network.api.DoclinksClient
import id.thork.app.network.api.WorkOrderApi
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.dao.AttachmentDao
import id.thork.app.persistence.dao.MultiAssetDao
import id.thork.app.persistence.dao.WoCacheDao
import okhttp3.logging.HttpLoggingInterceptor

class WorkerRepository constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val woCacheDao: WoCacheDao,
    private val appSession: AppSession,
    private val assetDao: AssetDao,
    private val attachmentDao: AttachmentDao,
    private val doclinksClient: DoclinksClient
) {

    fun buildWorkorderRepository(): WorkOrderRepository {
        val workOrderClient = WorkOrderClient(provideWorkOrderApi())
        val attachmentRepository = AttachmentRepository(context, preferenceManager, attachmentDao, doclinksClient)
        return WorkOrderRepository(
            workOrderClient,
            woCacheDao,
            appSession,
            assetDao,
            attachmentRepository
        )
    }

    private fun provideWorkOrderApi(): WorkOrderApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(WorkOrderApi::class.java)
    }

    fun buildAttachmentRepository(): AttachmentRepository {
        val doclinksClient = DoclinksClient(provideDoclinksApi())
        return AttachmentRepository(context, preferenceManager, attachmentDao, doclinksClient)
    }

    private fun provideDoclinksApi(): DoclinksApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(DoclinksApi::class.java)
    }
}