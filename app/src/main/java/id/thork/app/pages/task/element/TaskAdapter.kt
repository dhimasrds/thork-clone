package id.thork.app.pages.task.element

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewListTaskBinding
import id.thork.app.pages.task.CreateTaskActivity
import id.thork.app.persistence.entity.TaskEntity
import id.thork.app.utils.StringUtils
import timber.log.Timber

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
class TaskAdapter constructor(
    private val context: Context,
    private val taskEntity: List<TaskEntity>,
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardviewListTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(val binding: CardviewListTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(taskEntity: TaskEntity) {
            binding.apply {
                val status = taskEntity.status
                tvIdTask.text = taskEntity.taskId.toString()
                tvDescTask.text = StringUtils.truncate(taskEntity.desc, 115)
                when (status) {
                    BaseParam.APPROVED -> {
                        tvPriorityTask.text = BaseParam.APPROVED
                        tvPriorityTask.setTextColor(
                            ContextCompat.getColor(
                                tvPriorityTask.context,
                                R.color.blueTextStatus
                            )
                        )
                        tvPriorityTask.setBackgroundResource(R.drawable.bg_status)

                    }
                    BaseParam.INPROGRESS -> {
                        tvPriorityTask.text = BaseParam.INPROGRESS
                        tvPriorityTask.setTextColor(
                            ContextCompat.getColor(
                                tvPriorityTask.context,
                                R.color.colorYellow
                            )
                        )
                        tvPriorityTask.background =
                            ContextCompat.getDrawable(
                                tvPriorityTask.context,
                                R.drawable.bg_status_yellow
                            )
                    }
                    BaseParam.COMPLETED -> {
                        tvPriorityTask.text = BaseParam.COMPLETED
                        tvPriorityTask.setTextColor(
                            ContextCompat.getColor(
                                tvPriorityTask.context,
                                R.color.colorGreen
                            )
                        )
                        tvPriorityTask.setBackgroundResource(R.drawable.bg_status_green)
                    }
                    BaseParam.WAPPR -> {
                        tvPriorityTask.text = BaseParam.WAPPR
                        tvPriorityTask.setTextColor(
                            ContextCompat.getColor(
                                tvPriorityTask.context,
                                R.color.blueTextStatus
                            )
                        )
                        tvPriorityTask.setBackgroundResource(R.drawable.bg_status)
                    }
                }
                cardTask.setOnClickListener {
                    val intent = Intent(context, CreateTaskActivity::class.java)
                    val bundle = Bundle()
                    intent.putExtra(BaseParam.WORKORDERID, taskEntity.woId)
                    intent.putExtra(BaseParam.WONUM, taskEntity.wonum)
                    intent.putExtra(BaseParam.DESCRIPTION, taskEntity.desc)
                    intent.putExtra(BaseParam.STATUS, taskEntity.status)
                    intent.putExtra(BaseParam.SHEDULE_START, taskEntity.scheduleStart)
                    intent.putExtra(BaseParam.ACTUAL_START, taskEntity.actualStart)
                    intent.putExtra(BaseParam.TASKID, taskEntity.taskId)
                    intent.putExtra(BaseParam.ESTDUR, taskEntity.estDuration)
                    intent.putExtra(BaseParam.DETAIL_TASK, BaseParam.DETAIL_TASK)
                    Timber.d("raka woid %s, taskid %s ", taskEntity.woId, taskEntity.taskId)
                    startActivity(context, intent, bundle)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskEntity: TaskEntity = taskEntity[position]
        holder.bind(taskEntity)
    }

    override fun getItemCount(): Int {
        return taskEntity.size
    }

}