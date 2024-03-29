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
    const val APP_DATE_FORMAT = "dd/MM/yyyy"
    const val APP_TIME_FORMAT = "HH:mm"
    const val APP_DATETIME_FORMAT = "dd/MM/yyyy HH:mm"
    const val APP_DATETIME_FORMAT_OBJECTBOX = "yyyy-MM-dd'T'HH:mm:ss"

    private val DATE_FORMAT = "dd-MMM-yyyy HH:mm"
    const val DATE_FORMAT_MAXIMO = "yyyy-MM-dd'T'HH:mm:ss"
    const val DATE_FORMAT_OBJECTBOX = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z"
    private val DATE_ATTENDANCE = "EEEE, dd MMM yyyy"
    private val DATE_TIME_ATTENDANCE = "HH:mm"
    private val HOURS_ATTENDANCE = "hh:mm"
    private val DATE_CARD_ATTENDANCE = "dd/MMM/yyyy"
    private val DATE_FORMAT_BODYMX = "yyyy-MM-dd"
    private val DATE_TIME_FORMAT_BODYMX = "HH:mm:ss"
    private val DATE_TASK_FORMAT_MX = "yyyy-MM-dd'T'HH:mm:ssZ"
    private val DATE_TASK_FORMAT_OBJECTBOX = "yyyy-MM-dd'T'HH:mm:ss"
    private val DATE_ONLY_FORMAT_MAXIMO = "yyyy-MM-dd"


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

    fun convertDateToMillisec(dateString: String?) : Long {
        val date = SimpleDateFormat(APP_DATE_FORMAT).parse(dateString)
        return date.time
    }

    fun convertTimeToMillisec(dateString: String?) : Long {
        val date = SimpleDateFormat(APP_TIME_FORMAT).parse(dateString)
        return date.time
    }

    fun convertMaximoDateToDate(dateString: String?) : String {
        val date = SimpleDateFormat(DATE_FORMAT_MAXIMO).parse(dateString)
        val sdf = SimpleDateFormat(APP_DATE_FORMAT)
        return sdf.format(date.time)
    }

    fun convertMaximoDateToTime(dateString: String?) : String{
        val date = SimpleDateFormat(DATE_FORMAT_MAXIMO).parse(dateString)
        val sdf = SimpleDateFormat(APP_TIME_FORMAT)
        return sdf.format(date.time)
    }

    fun convertDateToString(millisec: Long) : String {
        val c = Date(millisec)
        val sdf = SimpleDateFormat(APP_DATE_FORMAT)
        return sdf.format(c.time)
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

    fun getAppDateFormat(date: Date): String {
        try {
            val format = SimpleDateFormat(APP_DATE_FORMAT)
            return format.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }

    fun getAppDateFormat(dateString: String?): Date {
        val date = SimpleDateFormat(APP_DATE_FORMAT).parse(dateString)
        return date
    }

    fun getAppTimeFormat(date: Date): String {
        try {
            val format = SimpleDateFormat(APP_TIME_FORMAT)
            return format.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }

    fun getAppDateFormatMaximo(date: Date): String {
        try {
            val format = SimpleDateFormat(DATE_ONLY_FORMAT_MAXIMO)
            return format.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }

    fun getAppTimeFormatMaximo(date: Date): String {
        try {
            val format = SimpleDateFormat(DATE_TIME_FORMAT_BODYMX)
            return format.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }


    fun getAppDateTimeOBFormat(date: Date): String {
        try {
            val format = SimpleDateFormat(APP_DATETIME_FORMAT_OBJECTBOX)
            return format.format(date)
//            dateFormated = dateFormated.substring(0, dateFormated.length-2)
//                .plus(":").plus(dateFormated.substring(dateFormated.length-2))
//            return dateFormated
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }

    fun convertDateTaskFormat(isCreateWO: Boolean, dateString: String?): String? {
        try {
            val dt = if (isCreateWO) {
                SimpleDateFormat(DATE_TASK_FORMAT_MX)
            } else {
                SimpleDateFormat(DATE_TASK_FORMAT_OBJECTBOX)
            }
            val date = dt.parse(dateString)
            val dateFormater: DateFormat = SimpleDateFormat(APP_DATE_FORMAT)
            return dateFormater.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }

    fun convertTimeTaskFormat(isCreateWO: Boolean, dateTime: String?): String? {
        try {
            val dt = if (isCreateWO) {
                SimpleDateFormat(DATE_TASK_FORMAT_MX)
            } else {
                SimpleDateFormat(DATE_TASK_FORMAT_OBJECTBOX)
            }
            val date = dt.parse(dateTime)
            val dateFormater: DateFormat = SimpleDateFormat(APP_TIME_FORMAT)
            return dateFormater.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return BaseParam.APP_EMPTY_STRING
    }
}