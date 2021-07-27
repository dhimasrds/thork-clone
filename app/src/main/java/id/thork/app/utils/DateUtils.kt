package id.thork.app.utils

import android.os.Build
import androidx.annotation.RequiresApi
import id.thork.app.base.BaseParam
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Raka Putra on 1/20/21
 * Jakarta, Indonesia.
 */
object DateUtils {
    private val DEFAULT_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss"
    private val DATE_FORMAT = "dd-MMM-yyyy HH:mm"
    private val DATE_FORMAT_MAXIMO = "yyyy-MM-dd'T'HH:mm:ss"
    private val DATE_FORMAT_OBJECTBOX = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z"
    private val DATE_ATTENDANCE = "EEEE, dd MMM yyyy"
    private val DATE_TIME_ATTENDANCE = "HH:mm"
    private val HOURS_ATTENDANCE = "hh:mm"
    private val DATE_CARD_ATTENDANCE = "dd/MMM/yyyy"
    private val DATE_FORMAT_BODYMX = "yyyy-MM-dd"
    private val DATE_TIME_FORMAT_BODYMX = "HH:mm:ss"

    /**
     * Get Time Interval from start date and end date
     *
     * @param startDate
     * @param endDate
     * @return
     */
    fun getTimeIntervalByDate(startDate: Date, endDate: Date): Double {
        return Math.floor((startDate.time - endDate.time).toDouble())
    }

    /**
     * Get Time Interval from start date (millisecond) and end date (millisecond)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    fun getTimeInterval(startTime: Long, endTime: Long): Double {
        return Math.floor((endTime - startTime).toDouble())
    }


    fun getTimestap(): String? {
        val timestamp = Timestamp(System.currentTimeMillis())
        return timestamp.time.toString()
    }

    fun getDateTime(): String? {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_FORMAT)
        return sdf.format(c.time)
    }

    fun getDateTimeMaximo(): String? {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_FORMAT_MAXIMO)
        return sdf.format(c.time)
    }

    fun getCurrentTimeMillisec(): Long {
        val c = Date()
        return c.time
    }

    fun convertMaximoDateToMillisec(dateString: String?): Long {
        val date = SimpleDateFormat(DATE_FORMAT_MAXIMO).parse(dateString)
        return date.time
    }

    /**
     * HEADER ATTENDANCE
     * For Display Header Attendance
     */
    fun getDateTimeHeaderAttendance(): String {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_ATTENDANCE)
        return sdf.format(c.time)
    }

    fun convertMaximoDateToHeaderAttendance(dateString: String?): String {
        val date = SimpleDateFormat(DATE_FORMAT_MAXIMO).parse(dateString)
        val sdf = SimpleDateFormat(DATE_ATTENDANCE)
        return sdf.format(date)
    }

    /**
     * CARDVIEW
     * For get Time to Display Check In And Check Out Attendance
     * And Date in Cardview Attendance
     */
    fun getCheckAttendance(millisec: Long): String? {
        val c = Date(millisec)
        val sdf = SimpleDateFormat(DATE_TIME_ATTENDANCE)
        return sdf.format(c.time)
    }

    fun getDateTimeCardView(millisec: Long): String? {
        val c = Date(millisec)
        val sdf = SimpleDateFormat(DATE_CARD_ATTENDANCE)
        return sdf.format(c.time)
    }

    /**
     * OBJECTBOX
     * save Date And Time in ObjectBox
     * for update to maximo
     */
    fun getDateAttendanceMaximo(millisec: Long): String {
        val c = Date(millisec)
        val sdf = SimpleDateFormat(DATE_FORMAT_BODYMX)
        return sdf.format(c.time)
    }

    fun getTimeAttendanceMaximo(millisec: Long): String {
        val c = Date(millisec)
        val sdf = SimpleDateFormat(DATE_TIME_FORMAT_BODYMX)
        return sdf.format(c.time)
    }

    /**
     * Please Read before u use this utils
     * For get WorkHours in Attendance
     * How to get WorkHours (dateCheckoutLocal - dateCheckInLocal)
     */
    fun getWorkHours(millisec: Long): String {
        val second: Long = millisec / 1000
        val minutes: Long = second / 60 % 60
        val hours: Long = second / (60 * 60) % 24
        return String.format("%02d:%02d", hours, minutes)
    }


    fun getDateTimeOB(date: Date?): String {
        try {
            val formatter = SimpleDateFormat(DATE_FORMAT_OBJECTBOX)
            formatter.applyPattern(DEFAULT_DATE_FORMAT)
            return formatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun convertDateFormat(dateString: String?): String? {
        try {
            val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat(BaseParam.REPORT_DATE_FORMAT)
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            val date = formatter.parse(dateString)
            val dateFormater: DateFormat = SimpleDateFormat(DATE_FORMAT)
            return dateFormater.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }

    fun convertStringToMaximoDate(dateString: String?): Date {
        val date = SimpleDateFormat(DATE_FORMAT_MAXIMO).parse(dateString)
        return date
    }

    fun convertMxDateStringToString(date: Date): String {
        try {
            val formatter = SimpleDateFormat(DATE_FORMAT_MAXIMO)
            formatter.applyPattern(DEFAULT_DATE_FORMAT)
            return formatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }
}