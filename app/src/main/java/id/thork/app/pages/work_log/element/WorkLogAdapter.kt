package id.thork.app.pages.work_log.element

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewListWorklogBinding
import id.thork.app.pages.work_log.detail_work_log.DetailWorkLogActivity
import id.thork.app.persistence.entity.WorklogEntity
import id.thork.app.utils.StringUtils

/**
 * Created by Dhimas Saputra on 07/06/21
 * Jakarta, Indonesia.
 */
class WorkLogAdapter constructor(
    private val activity: Activity,
    private val worklogEntity: List<WorklogEntity>
) :
    RecyclerView.Adapter<WorkLogAdapter.ViewHolder>() {
    val TAG = WorkLogAdapter::class.java.name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardviewListWorklogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(val binding: CardviewListWorklogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(worklogEntity: WorklogEntity) {
            binding.apply {
                tvTitle.text = StringUtils.truncate(worklogEntity.summary, 20)
                tvDesc.text = StringUtils.truncate(worklogEntity.description, 115)
                tvType.text = worklogEntity.type
                tvDate.text = worklogEntity.date
                cardWorklog.setOnClickListener {
                    val intent = Intent(activity, DetailWorkLogActivity::class.java)
                    intent.putExtra(BaseParam.WONUM, worklogEntity.wonum)
                    intent.putExtra(BaseParam.SUMMARY, worklogEntity.summary)
                    activity.startActivity(intent)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val worklogEntity: WorklogEntity = worklogEntity[position]
        holder.bind(worklogEntity)
    }

    override fun getItemCount(): Int {
        return worklogEntity.size
    }


}
