package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.AssetEntity_
import id.thork.app.persistence.entity.AttendanceEntity
import id.thork.app.persistence.entity.AttendanceEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
class AttendanceDaoImp : AttendanceDao {
    val TAG = AttendanceDaoImp::class.java.name

    var attendanceEntityBox: Box<AttendanceEntity>

    init {
        attendanceEntityBox = ObjectBox.boxStore.boxFor(AttendanceEntity::class.java)
    }

    private fun addUpdateInfo(attendanceEntity: AttendanceEntity, username: String?) {
        attendanceEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                attendanceEntity.createdDate = Date()
                attendanceEntity.createdBy = username
            }
        )
        attendanceEntity.updatedDate = Date()
        attendanceEntity.updatedBy = username
    }

    override fun remove() {
        attendanceEntityBox.removeAll()
    }

    override fun createAttendanceCache(attendanceEntity: AttendanceEntity, username: String?){
        addUpdateInfo(attendanceEntity, username)
        attendanceEntityBox.put(attendanceEntity)

    }

    override fun findCheckInAttendance(): AttendanceEntity? {
        val attendanceEntity =
            attendanceEntityBox.query().notNull(AttendanceEntity_.dateCheckIn).build().find()
        attendanceEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }





}