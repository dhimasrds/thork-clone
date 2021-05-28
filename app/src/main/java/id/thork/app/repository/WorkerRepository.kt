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
import id.thork.app.persistence.dao.*
import okhttp3.logging.HttpLoggingInterceptor

class WorkerRepository constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val woCacheDao: WoCacheDao,
    private val appSession: AppSession,
    private val assetDao: AssetDao,
    private val attachmentDao: AttachmentDao,
    private val doclinksClient: DoclinksClient,
    private val materialBackupDao: MaterialBackupDao,
    private val matusetransDao: MatusetransDao,
    private val wpmaterialDao: WpmaterialDao,
    private val materialDao: MaterialDao,
) {

    fun buildWorkorderRepository(): WorkOrderRepository {
        val workOrderClient = WorkOrderClient(provideWorkOrderApi())
        val attachmentRepository =
            AttachmentRepository(context, preferenceManager, attachmentDao, httpLoggingInterceptor)
        val materialRepository = MaterialRepository(
            materialBackupDao,
            matusetransDao,
            wpmaterialDao,
            materialDao,
            appSession
        )
        return WorkOrderRepository(
            workOrderClient,
            woCacheDao,
            appSession,
            assetDao,
            attachmentRepository,
            materialRepository
        )
    }

    private fun provideWorkOrderApi(): WorkOrderApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(WorkOrderApi::class.java)
    }

    fun buildAttachmentRepository(): AttachmentRepository {
        return AttachmentRepository(
            context,
            preferenceManager,
            attachmentDao,
            httpLoggingInterceptor
        )
    }

    private fun provideDoclinksApi(): DoclinksApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(DoclinksApi::class.java)
    }

    fun buildMaterialRepository(): MaterialRepository {
        return MaterialRepository(
            materialBackupDao,
            matusetransDao,
            wpmaterialDao,
            materialDao,
            appSession
        )
    }


}