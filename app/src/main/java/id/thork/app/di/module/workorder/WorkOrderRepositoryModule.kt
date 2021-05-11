package id.thork.app.di.module.workorder

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.AppSession
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.dao.AssetDaoImp
import id.thork.app.persistence.dao.MaterialDaoImp
import id.thork.app.persistence.dao.WoCacheDaoImp
import id.thork.app.repository.MaterialRepository
import id.thork.app.repository.WoActivityRepository
import id.thork.app.repository.WorkOrderRepository

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
    ): WorkOrderRepository {
        return WorkOrderRepository(workOrderClient,WoCacheDaoImp(), appSession, AssetDaoImp())
    }

    @Provides
    @ActivityRetainedScoped
    fun provideWorkOrderActRepository(
        workOrderClient: WorkOrderClient,
    ): WoActivityRepository {
        return WoActivityRepository(workOrderClient, WoCacheDaoImp(), AssetDaoImp())
    }

    @Provides
    @ActivityRetainedScoped
    fun provideMaterialRepository(
        workOrderClient: WorkOrderClient,
    ): MaterialRepository {
        return MaterialRepository(workOrderClient, MaterialDaoImp())
    }
}