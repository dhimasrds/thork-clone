package id.thork.app.pages.profiles.attendance.element

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.databinding.CardviewAttendanceBinding
import id.thork.app.databinding.CardviewListWorklogBinding
import id.thork.app.pages.find_asset_location.element.FindAssetAdapter
import id.thork.app.pages.work_log.element.WorkLogAdapter
import id.thork.app.persistence.entity.WorklogEntity
import id.thork.app.utils.StringUtils

/**
 * Created by Dhimas Saputra on 14/06/21
 * Jakarta, Indonesia.
 */
class HistoryAttendanceAdapter constructor(

) :
    RecyclerView.Adapter<HistoryAttendanceAdapter.ViewHolder>() {
    val TAG = FindAssetAdapter::class.java.name


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardviewAttendanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(val binding: CardviewAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {

            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 0
    }


}
