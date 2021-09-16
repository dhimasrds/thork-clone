package id.thork.app.pages.labor_plan.element

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewLaborAndCraftBinding
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.persistence.entity.TaskEntity

/**
 * Created by M.Reza Sulaiman on 08/08/2021
 * Jakarta, Indonesia.
 */
class LaborTaskAdapter constructor(
    private val activity: Activity,
    private val taskEntities: List<TaskEntity>
) :
    RecyclerView.Adapter<LaborTaskAdapter.ViewHolder>() {
    val TAG = LaborTaskAdapter::class.java.name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardviewLaborAndCraftBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(val binding: CardviewLaborAndCraftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(taskEntity: TaskEntity) {
            binding.apply {
                tvLaborOrCraft.text = taskEntity.taskId.toString().plus(BaseParam.APP_DASH)
                    .plus(taskEntity.desc.toString())
                cardLaborPlan.setOnClickListener {
                    val intent =
                        Intent(BaseApplication.context, CreateLaborPlanActivity::class.java)
                    intent.putExtra(BaseParam.DESCRIPTION, taskEntity.desc)
                    intent.putExtra(BaseParam.TASKID, taskEntity.taskId)
                    intent.putExtra(BaseParam.REFWONUM, taskEntity.refWonum)
                    activity.setResult(AppCompatActivity.RESULT_OK, intent)
                    activity.finish()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskEntity: TaskEntity = taskEntities[position]
        holder.bind(taskEntity)
    }

    override fun getItemCount(): Int {
        return taskEntities.size
    }
}