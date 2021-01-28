package id.thork.app.di.module.workorder

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import id.thork.app.di.module.AppSession
import id.thork.app.network.api.LoginClient
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.persistence.dao.UserDaoImp
import id.thork.app.persistence.dao.WoCacheDaoImp
import id.thork.app.repository.LoginRepository
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
    ): WorkOrderRepository {
        return WorkOrderRepository(workOrderClient,WoCacheDaoImp())
    }
}