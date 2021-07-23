package id.thork.app.utils

import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import id.thork.app.network.response.work_order.Member
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by M.Reza Sulaiman on 17/02/21
 * Jakarta, Indonesia.
 */
object WoUtils {
    private val DATE_TIME_FORMAT_REFID = "HHmmssSSS"

    fun convertBodyToMember(syncBody: String): Member? {
        val moshi = Moshi.Builder().build()
        val memberJsonAdapter: JsonAdapter<Member> = moshi.adapter<Member>(
            Member::class.java
        )
        return memberJsonAdapter.fromJson(syncBody)
    }

    fun convertMemberToBody(member: Member): String? {
        val gson = Gson()
        return gson.toJson(member)
    }

    /**
     * Generate user external REFID
     */

    fun getExternalRefid(): String {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_TIME_FORMAT_REFID)
        return sdf.format(c.time)
    }


}