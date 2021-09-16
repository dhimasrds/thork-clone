package id.thork.app.di.module.workorder

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.MaterialClient
import id.thork.app.network.api.StoreroomClient
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
        storeroomRepository: StoreroomRepository,
        worklogRepository: WorklogRepository,
        taskRepository: TaskRepository,
        laborRepository: LaborRepository
    ): WorkOrderRepository {
        return WorkOrderRepository(
            context,
            workOrderClient, WoCacheDaoImp(), appSession, AssetDaoImp(),
            attachmentRepository, materialRepository, storeroomRepository, worklogRepository,
            taskRepository, laborRepository
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
    fun provideWpMaterialRepository(
        appSession: AppSession,
    ): WpMaterialRepository {
        return WpMaterialRepository(
            WpmaterialDaoImp(),
            appSession
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideMaterialActualRepository(
        materialClient: MaterialClient,
        appSession: AppSession,
    ): MaterialActualRepository {
        return MaterialActualRepository(
            MaterialActualDaoImp(),
            materialClient,
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
        return WorklogRepository(
            WorklogDaoImp(),
            WorklogTypeDaoImp(),
            appSession
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideTaskRepository(
        appSession: AppSession,
        preferenceManager: PreferenceManager,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): TaskRepository {
        return TaskRepository(
            appSession,
            TaskDaoImp(),
            httpLoggingInterceptor,
            preferenceManager,
            LaborPlanDaoImp(),
            LaborActualDaoImp(),
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideStoreroomRepository(
        context: Context,
        storeroomClient: StoreroomClient,
        appSession: AppSession
    ): StoreroomRepository {
        return StoreroomRepository(context, storeroomClient,
            StoreroomDaoImp(), MaterialStoreroomDaoImp(), appSession
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideMaterialStoreroomRepository(
        appSession: AppSession,
        preferenceManager: PreferenceManager,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): MaterialStoreroomRepository {
        return MaterialStoreroomRepository(
            MaterialStoreroomDaoImp(), appSession
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideLaborRepository(
        appSession: AppSession,
    ): LaborRepository {
        return LaborRepository(
            appSession,
            LaborPlanDaoImp(),
            LaborActualDaoImp(),
            LaborMasterDaoImp(),
        CraftMasterDaoImp())
    }
}