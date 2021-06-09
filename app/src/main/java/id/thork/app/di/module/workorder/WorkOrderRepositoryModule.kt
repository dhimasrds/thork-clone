package id.thork.app.di.module.workorder

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.persistence.dao.*
import id.thork.app.repository.*
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */

@Module
@InstallIn(ActivityRetainedComponent::class)
object WorkOrderRepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideWorkOrderRepository(
        context: Context,
        workOrderClient: WorkOrderClient,
        appSession: AppSession,
        attachmentRepository: AttachmentRepository,
        materialRepository: MaterialRepository,
        worklogRepository: WorklogRepository
    ): WorkOrderRepository {
        return WorkOrderRepository(context,
            workOrderClient, WoCacheDaoImp(), appSession, AssetDaoImp(),
            attachmentRepository, materialRepository, worklogRepository
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideWorkOrderActRepository(
        workOrderClient: WorkOrderClient,
    ): WoActivityRepository {
        return WoActivityRepository(workOrderClient, WoCacheDaoImp())
    }

    @Provides
    @ActivityRetainedScoped
    fun provideMaterialRepository(
        appSession: AppSession,
    ): MaterialRepository {
        return MaterialRepository(
            MaterialBackupDaoImp(),
            MatusetransDaoImp(),
            WpmaterialDaoImp(),
            MaterialDaoImp(),
            appSession
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideAttachmentRepository(
        context: Context,
        preferenceManager: PreferenceManager,
        appSession: AppSession,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): AttachmentRepository {
        return AttachmentRepository(
            context,
            preferenceManager,
            appSession,
            AttachmentDaoImp(),
            httpLoggingInterceptor
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideWorklogRepository(
        appSession: AppSession
    ): WorklogRepository {
        return  WorklogRepository(
            WorklogDaoImp(),
            WorklogTypeDaoImp(),
            appSession
        )
    }
}