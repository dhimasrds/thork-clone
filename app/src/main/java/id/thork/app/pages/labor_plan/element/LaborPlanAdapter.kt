package id.thork.app.pages.labor_plan.element

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewLaborPlanBinding
import id.thork.app.databinding.CardviewListWorklogBinding
import id.thork.app.pages.find_asset_location.element.FindAssetAdapter
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.pages.labor_plan.details_labor_plan.LaborPlanDetailsActivity
import id.thork.app.pages.work_log.detail_work_log.DetailWorkLogActivity
import id.thork.app.pages.work_log.element.WorkLogAdapter
import id.thork.app.persistence.entity.LaborPlanEntity
import id.thork.app.persistence.entity.WorklogEntity
import id.thork.app.utils.StringUtils

/**
 * Created by Dhimas Saputra on 23/06/21
 * Jakarta, Indonesia.
 */
class LaborPlanAdapter constructor(
    private val activity : Activity,
    private val laborPlanEntity: List<LaborPlanEntity>

) :
    RecyclerView.Adapter<LaborPlanAdapter.ViewHolder>() {
    val TAG = LaborPlanAdapter::class.java.name


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardviewLaborPlanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(val binding: CardviewLaborPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(laborPlanEntity: LaborPlanEntity) {
            val vendor = StringUtils.NVL(laborPlanEntity.vendor, BaseParam.APP_DASH)
            val laborcode = StringUtils.NVL(laborPlanEntity.laborcode, BaseParam.APP_DASH)
            val taskid =  StringUtils.NVL(laborPlanEntity.taskid, BaseParam.APP_DASH)
            val taskdesc =  StringUtils.NVL(laborPlanEntity.taskDescription, BaseParam.APP_DASH)
            binding.apply {
                if (laborcode != BaseParam.APP_DASH) {
                    tvLabor.text = laborcode
                } else {
                    tvLabor.text = laborPlanEntity.craft
                }

                tvType.text = taskid.plus(BaseParam.APP_DASH).plus(taskdesc)
                tvStatus.text = vendor
                cardLaborPlan.setOnClickListener {
                    val intent = Intent(activity, LaborPlanDetailsActivity::class.java)
                    intent.putExtra(BaseParam.WONUM, laborPlanEntity.wonumHeader)
                    intent.putExtra(BaseParam.WORKORDERID, laborPlanEntity.workorderid)
                    intent.putExtra(BaseParam.LABORCODE, laborPlanEntity.laborcode)
                    intent.putExtra(BaseParam.CRAFT, laborPlanEntity.craft)
                    activity.startActivity(intent)
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laborPlanEntity: LaborPlanEntity = laborPlanEntity[position]
        holder.bind(laborPlanEntity)
    }

    override fun getItemCount(): Int {
        return laborPlanEntity.size
    }


}