package id.thork.app.workmanager

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.dao.AttendanceDao
import id.thork.app.persistence.entity.AttendanceEntity
import id.thork.app.repository.AttendanceRepository
import id.thork.app.repository.WorkerAttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    var attendanceRepository: AttendanceRepository

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
            syncAttendance()
            return Result.success()

        } catch (e: Exception) {
            Timber.tag(TAG).e("doWork() error: %s", e.message)
            return Result.retry()
        }
    }

    fun syncAttendance() {
        val attendanceCache = attendanceRepository.findAttendanceOfflineMode()
        attendanceCache.whatIfNotNull { cache ->
            cache.dateCheckOutLocal.whatIfNotNull(
                whatIf = {
                    updateCheckOut(cache)
                },
                whatIfNot = {
                    updateCheckIn(cache)
                }
            )
        }
    }

    private fun updateCheckIn(attendanceEntity: AttendanceEntity) {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val contentType: String = ("application/json")
        val properties: String = BaseParam.APP_ALL_PROPERTIES
        val member = attendanceRepository.prepareBodyCheckInOfflineMode(attendanceEntity)


        runBlocking {
            launch(Dispatchers.IO) {
                attendanceRepository.createAttendanceToMx(
                    cookie,
                    contentType,
                    properties,
                    member,
                    onSuccess = {
                        it.attendanceid.whatIfNotNull { attendanceid ->
                            attendanceRepository.handlingAttendanceCheckInOfflineMode(
                                attendanceEntity,
                                attendanceid
                            )
                        }

                    },
                    onError = {
                        Timber.tag(TAG).i("updateCheckIn() onError() onError: %s", it)
                    }
                )
            }
        }


    }

    private fun updateCheckOut(attendanceEntity: AttendanceEntity) {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val xMethodeOverride: String = BaseParam.APP_PATCH
        val contentType: String = ("application/json")
        val patchType: String = BaseParam.APP_MERGE
        val attendanceId = attendanceEntity.attendanceId
        val member = attendanceRepository.prepareBodyCheckOutOfflineMode(attendanceEntity)

        runBlocking {
            launch (Dispatchers.IO){
                attendanceId.whatIfNotNull {
                    attendanceRepository.updateAttendanceToMx(
                        cookie,
                        xMethodeOverride,
                        contentType,
                        patchType,
                        it,
                        member,
                        onSuccess = {
                            attendanceRepository.handlingAttendanceCheckOutOfflineMode(attendanceEntity)
                        },
                        onError = {
                            Timber.tag(TAG).i("updateCheckOut() onError() onError: %s", it)

                        }
                    )
                }

            }
        }


    }
}