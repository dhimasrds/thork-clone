package id.thork.app.persistence.entity

import io.objectbox.annotation.Entity

/**
 * Created by M.Reza Sulaiman on 16/06/2021
 * Jakarta, Indonesia.
 */
@Entity
class AttendanceEntity : BaseEntity(){
    var dateCheckInLocal: Long? = null
    var dateCheckIn : String? = null
    var hoursCheckIn : String? = null
    var longCheckIn: String? = null
    var latCheckIn: String? = null
    var uriImageCheckIn: String? = null

    var dateCheckOutLocal: Long? = null
    var dateCheckOut : String? = null
    var hoursCheckOut : String? = null
    var longCheckOut: String? = null
    var latCheckOut: String? = null
    var uriImageCheckOut: String? = null

    var workHours: String? = null
    var dateTimeHeader: String? = null
    var username: String? = null
    var syncUpdate: Int? = null
    var offlineMode: Int? = null
    var attendanceId: Int? = null
}