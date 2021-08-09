package id.thork.app.repository

import android.content.Context
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.dao.AttendanceDao
import id.thork.app.persistence.dao.LaborActualDao
import id.thork.app.persistence.dao.LaborPlanDao
import id.thork.app.persistence.dao.TaskDao
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by Raka Putra on 7/16/21
 * Jakarta, Indonesia.
 */
class WorkerTaskRepository constructor(
    private val appSession: AppSession,
    private val taskDao: TaskDao,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val preferenceManager: PreferenceManager,
    private val laborPlanDao: LaborPlanDao,
    private val laborActualDao: LaborActualDao
) {
    fun buildTaskRepository(): TaskRepository {
        return TaskRepository(
            appSession, taskDao, httpLoggingInterceptor, preferenceManager, laborPlanDao, laborActualDao
        )
    }
}