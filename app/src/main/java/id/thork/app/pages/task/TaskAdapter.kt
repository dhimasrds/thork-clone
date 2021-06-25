package id.thork.app.pages.task

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.databinding.CardviewListTaskBinding
import id.thork.app.persistence.entity.TaskEntity
import id.thork.app.utils.StringUtils
import timber.log.Timber

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
                tvIdTask.text = taskEntity.taskId.toString()
                tvDescTask.text = StringUtils.truncate(taskEntity.desc, 115)
                tvPriorityTask.text = taskEntity.status
                Timber.d("raka %s ", taskEntity.taskId)
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