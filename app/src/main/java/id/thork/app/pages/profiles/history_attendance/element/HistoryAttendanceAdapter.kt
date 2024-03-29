package id.thork.app.pages.profiles.history_attendance.element

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.databinding.HistoryCardviewAttendanceBinding
import id.thork.app.pages.find_asset_location.element.FindAssetAdapter
import id.thork.app.pages.profiles.history_attendance.HistoryAttendanceActivity
import id.thork.app.pages.profiles.history_attendance.HistoryAttendanceDetailsActivity
import id.thork.app.persistence.entity.AttendanceEntity
import id.thork.app.utils.DateUtils
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 14/06/21
 * Jakarta, Indonesia.
 */
class HistoryAttendanceAdapter constructor(
    private val context: Context,
    private val attendanceActivity: HistoryAttendanceActivity,
    private val attendanceEntities: List<AttendanceEntity>,
) :
    RecyclerView.Adapter<HistoryAttendanceAdapter.ViewHolder>() {
    val TAG = FindAssetAdapter::class.java.name


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HistoryCardviewAttendanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attendanceEntity: AttendanceEntity = attendanceEntities[position]
        holder.bind(attendanceEntity)
    }

    inner class ViewHolder(val binding: HistoryCardviewAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attendanceEntity: AttendanceEntity) {
            binding.cardHistoryAttendance.isClickable = true
            binding.root.isEnabled = true
            binding.apply {
                attendanceEntity.dateCheckOutLocal.whatIfNotNull { dateCheckOutLocal ->
                    attendanceEntity.dateCheckInLocal.whatIfNotNull { dateCheckInLocal ->
                        Timber.tag(TAG).d(
                            "ViewHolder() dateCheckOutLocal: %s dateCheckInLocal: %s",
                            dateCheckOutLocal, dateCheckInLocal
                        )
                        tvCheckInDate.text = DateUtils.getDateTimeCardView(dateCheckInLocal)
                        tvCheckInTime.text = DateUtils.getCheckAttendance(dateCheckInLocal)
                        tvWorkHour.text = attendanceEntity.workHours
                        tvDateAttendance.text = attendanceEntity.dateTimeHeader
                        tvCheckOutDate.text = DateUtils.getDateTimeCardView(dateCheckOutLocal)
                        tvCheckOutTime.text = DateUtils.getCheckAttendance(dateCheckOutLocal)
                        root.setOnClickListener {
                            val intent =
                                Intent(context, HistoryAttendanceDetailsActivity::class.java)
                            val bundle = Bundle()
                            intent.putExtra(BaseParam.ATTENDANCEID, attendanceEntity.attendanceId)
                            startActivity(context, intent, bundle)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return attendanceEntities.size
    }
}
