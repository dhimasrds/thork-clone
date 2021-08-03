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
    private val worklogDao: WorklogDao,
    private val worklogTypeDao: WorklogTypeDao,
    private val taskDao: TaskDao,
    private val laborPlanDao: LaborPlanDao,
    private val laborActualDao: LaborActualDao,
    private val laborMasterDao: LaborMasterDao,
    private val craftMasterDao: CraftMasterDao
) {

    fun buildWorkorderRepository(): WorkOrderRepository {
        val workOrderClient = WorkOrderClient(provideWorkOrderApi())
        val attachmentRepository =
            AttachmentRepository(
                context,
                preferenceManager,
                appSession,
                attachmentDao,
                httpLoggingInterceptor
            )
        val materialRepository = MaterialRepository(
            materialBackupDao,
            matusetransDao,
            wpmaterialDao,
            materialDao,
            appSession
        )

        val worklogRepository = WorklogRepository(
            worklogDao, worklogTypeDao, appSession
        )

        val taskRepository =
            TaskRepository(appSession, taskDao, httpLoggingInterceptor, preferenceManager)

        val laborRepository = LaborRepository(
            appSession,
            laborPlanDao,
            laborActualDao,
            laborMasterDao,
            craftMasterDao
        )

        return WorkOrderRepository(
            context,
            workOrderClient,
            woCacheDao,
            appSession,
            assetDao,
            attachmentRepository,
            materialRepository,
            worklogRepository,
            taskRepository,
            laborRepository
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
            appSession,
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

    fun buildWorklogRepository(): WorklogRepository {
        return WorklogRepository(
            worklogDao, worklogTypeDao, appSession
        )
    }

    fun buildTaskRepository(): TaskRepository {
        return TaskRepository(
            appSession, taskDao, httpLoggingInterceptor, preferenceManager
        )
    }

    fun buildLaborRepository(): LaborRepository {
        return LaborRepository(
            appSession,
            laborPlanDao,
            laborActualDao,
            laborMasterDao,
            craftMasterDao
        )
    }


}