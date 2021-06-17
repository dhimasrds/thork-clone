package id.thork.app.repository

import android.content.Context
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.AttendanceApi
import id.thork.app.network.api.AttendanceClient
import id.thork.app.network.response.attendance_response.Member
import id.thork.app.persistence.dao.AttendanceDao
import id.thork.app.persistence.entity.AttendanceEntity
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
class AttendanceRepository @Inject constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val appSession: AppSession,
    private val attendanceDao: AttendanceDao,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
) : BaseRepository {
    val TAG = AttendanceRepository::class.java.name


    private val attendanceClient: AttendanceClient

    init {
        attendanceClient = AttendanceClient(provideAttendanceApi())
    }

    private fun provideAttendanceApi(): AttendanceApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(AttendanceApi::class.java)
    }


    var laborCode = appSession.userEntity.laborcode
    var username = appSession.userEntity.username
    private var attendanceEntity: AttendanceEntity? = null


    fun saveAttendanceCache(attendanceEntity: AttendanceEntity) {
        return attendanceDao.createAttendanceCache(attendanceEntity, username.toString())
    }

    fun removeAttendance() {
        return attendanceDao.remove()
    }

    fun findCheckInAttendance(): AttendanceEntity? {
        return attendanceDao.findCheckInAttendance()
    }

    fun findAttendanceBySyncUpdate(syncUpdate: Int): AttendanceEntity? {
        return attendanceDao.findAttendanceBySynUpdate(syncUpdate)
    }

    fun findAttendanceById(attendanceId: Int) : AttendanceEntity? {
        return attendanceDao.findAttendanceByAttendanceId(attendanceId)
    }

    fun findListAttendanceCacheOffline(offlinemode: Int, syncUpdate: Int) : List<AttendanceEntity> {
        return attendanceDao.findListAttendanceOfflineMode(offlinemode, syncUpdate)
    }

    fun findlistAttendanceCacheLocal() : List<AttendanceEntity> {
        return attendanceDao.findListAttendanceLocal()
    }

    fun prepareBodyCheckIn() : Member{
        val attandanceEntity = findAttendanceBySyncUpdate(BaseParam.APP_FALSE)
        val memberCheckIn = Member()
        attandanceEntity.whatIfNotNull {
            memberCheckIn.startdate = it.dateCheckIn
            memberCheckIn.starttime = it.hoursCheckIn
            memberCheckIn.thisfsmlongitudex = it.longCheckIn?.toDouble()
            memberCheckIn.thisfsmlatitudey = it.latCheckIn?.toDouble()
            memberCheckIn.orgid = appSession.orgId
            memberCheckIn.laborcode = it.username
        }
        return memberCheckIn
    }

    fun prepareBodyCheckOut(attendanceId: Int) : Member{
        val attendanceEntity = findAttendanceById(attendanceId)
        val memberCheckOut = Member()
        attendanceEntity.whatIfNotNull {
            memberCheckOut.finishdate = it.dateCheckOut
            memberCheckOut.finishtime = it.hoursCheckOut
            memberCheckOut.thisfsmlongitudexout = it.longCheckOut?.toDouble()
            memberCheckOut.thisfsmlatitudeyout = it.latCheckOut?.toDouble()
        }
        return memberCheckOut
    }

    fun handlingCheckInAfterUpdate(attendanceId: Int) {
        val attendanceEntity = findAttendanceBySyncUpdate(BaseParam.APP_FALSE)
        attendanceEntity.whatIfNotNull {
            it.attendanceId = attendanceId
            saveAttendanceCache(it)
        }
    }

    fun handlingCheckInFailed() {
        val attendanceEntity = findAttendanceBySyncUpdate(BaseParam.APP_FALSE)
        attendanceEntity.whatIfNotNull {
            it.offlineMode = BaseParam.APP_TRUE
            saveAttendanceCache(it)
        }
    }

    fun handlingCheckOutAfterUpdate(attendanceId: Int) {
        val attendanceEntity = findAttendanceById(attendanceId)
        attendanceEntity.whatIfNotNull {
            it.syncUpdate = BaseParam.APP_TRUE
            saveAttendanceCache(it)
        }
    }

    fun handlingCheckOutFailed(attendanceId: Int) {
        val attendanceEntity = findAttendanceById(attendanceId)
        attendanceEntity.whatIfNotNull {
            it.offlineMode = BaseParam.APP_TRUE
            saveAttendanceCache(it)
        }
    }

    fun handlingAttendanceOfflineMode(attendanceEntity: AttendanceEntity) {
        attendanceEntity.offlineMode = BaseParam.APP_FALSE
        saveAttendanceCache(attendanceEntity)
    }


    suspend fun createAttendanceToMx(
        cookie: String,
        contentType: String,
        properties: String,
        prepareBody: Member,
        onSuccess: (Member) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = attendanceClient.createAttendance(
            cookie, contentType, properties,
            prepareBody
        )

        response.suspendOnSuccess {
            data.whatIfNotNull {
                onSuccess(it)
            }
            Timber.tag(TAG).i("createAttendanceToMx() code: %s ", statusCode.code)

        }
            .onError {
                Timber.tag(TAG).i(
                    "createAttendanceToMx() code: %s error: %s",
                    statusCode.code,
                    message()
                )
                onError(message())
            }
    }


    suspend fun updateAttendanceToMx(
        cookie: String,
        methodoverride: String,
        contentType: String,
        patchType: String,
        attendanceId: Int,
        prepareBody: Member,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val response = attendanceClient.updateAttendance(
            cookie,
            methodoverride,
            contentType,
            patchType,
            attendanceId,
            prepareBody
        )

        response.onSuccess {
            onSuccess()
            Timber.tag(TAG).i("updateAttendance() code: %s ", statusCode.code)
        }
            .onError {
                Timber.tag(TAG).i(
                    "updateAttendance() code: %s error: %s",
                    statusCode.code,
                    message()
                )
                onError(message())
            }
    }

    fun saveCache(
        isCheckin: Boolean, dateLocal: Long, date: String, hours: String,
        longitudex: String, latitudey: String, uriImage: String, dateTimeHeader: String,
        workHours: String?,
    ) {
        if (isCheckin) {
            val checkinEntity = AttendanceEntity(
                dateCheckInLocal = dateLocal,
                dateCheckIn = date,
                hoursCheckIn = hours,
                longCheckIn = longitudex,
                latCheckIn = latitudey,
                uriImageCheckIn = uriImage,
                dateTimeHeader = dateTimeHeader,
                username = laborCode,
                syncUpdate = BaseParam.APP_FALSE
            )
            saveAttendanceCache(checkinEntity)
        } else {
            attendanceEntity = findCheckInAttendance()
            attendanceEntity.whatIfNotNull {
                it.dateCheckOutLocal = dateLocal
                it.dateCheckOut = date
                it.hoursCheckOut = hours
                it.longCheckOut = longitudex
                it.latCheckOut = latitudey
                it.uriImageCheckOut = uriImage

                it.workHours = workHours
                it.offlineMode = BaseParam.APP_FALSE
                saveAttendanceCache(it)
            }
        }
    }
}