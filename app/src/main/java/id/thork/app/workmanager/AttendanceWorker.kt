package id.thork.app.workmanager

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.dao.AttendanceDao
import id.thork.app.repository.AttendanceRepository
import id.thork.app.repository.WorkerAttendanceRepository
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 17/06/2021
 * Jakarta, Indonesia.
 */
class AttendanceWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    val appSession: AppSession,
    val preferenceManager: PreferenceManager,
    val httpLoggingInterceptor: HttpLoggingInterceptor,
    val attendanceDao: AttendanceDao,
) :
    Worker(context, workerParameters) {
    private val TAG = LocationWorker::class.java.name

    var attendanceRepository : AttendanceRepository

    init {
        val workAttendanceRepository = WorkerAttendanceRepository(
            context, preferenceManager, httpLoggingInterceptor, appSession, attendanceDao,

        )
        attendanceRepository = workAttendanceRepository.buildAttendanceRepository()
    }

    private val MAX_RUN_ATTEMPT = 6

    override fun doWork(): Result {
        try {
            //Query Local WO Record is needed to sync with the server
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }

            return Result.success()

        } catch (e: Exception) {
            Timber.tag(TAG).e("doWork() error: %s", e.message)
            return Result.retry()
        }
    }

    fun syncAttendance() {

    }
}