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
import id.thork.app.persistence.dao.AssetDaoImp
import id.thork.app.persistence.dao.AttachmentDaoImp
import id.thork.app.persistence.dao.MaterialDaoImp
import id.thork.app.persistence.dao.WoCacheDaoImp
import id.thork.app.repository.AttachmentRepository
import id.thork.app.repository.MaterialRepository
import id.thork.app.repository.WoActivityRepository
import id.thork.app.repository.WorkOrderRepository
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
        workOrderClient: WorkOrderClient,
        appSession: AppSession,
        attachmentRepository: AttachmentRepository
    ): WorkOrderRepository {
        return WorkOrderRepository(
            workOrderClient, WoCacheDaoImp(), appSession, AssetDaoImp(),
            attachmentRepository
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
        workOrderClient: WorkOrderClient,
    ): MaterialRepository {
        return MaterialRepository(workOrderClient, MaterialBackupDaoImp())
    }

    @Provides
    @ActivityRetainedScoped
    fun provideAttachmentRepository(
        context: Context,
        preferenceManager: PreferenceManager,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): AttachmentRepository {
        return AttachmentRepository(context, preferenceManager, AttachmentDaoImp(), httpLoggingInterceptor)
    }
}