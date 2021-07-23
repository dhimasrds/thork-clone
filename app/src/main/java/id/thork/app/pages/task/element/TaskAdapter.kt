package id.thork.app.pages.task.element

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewListTaskBinding
import id.thork.app.persistence.entity.TaskEntity
import id.thork.app.utils.StringUtils

/**
 * Created by Raka Putra on 6/23/21
 * Jakarta, Indonesia.
 */
class TaskAdapter constructor(
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