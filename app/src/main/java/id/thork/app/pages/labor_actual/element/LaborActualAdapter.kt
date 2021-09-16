package id.thork.app.pages.labor_actual.element

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewLaborPlanBinding
import id.thork.app.pages.labor_actual.details_labor_actual.LaborActualDetailsActivity
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.persistence.entity.LaborActualEntity
import id.thork.app.utils.StringUtils

/**
 * Created by Dhimas Saputra on 05/08/21
 * Jakarta, Indonesia.
 */
class LaborActualAdapter constructor(
    private val activity : Activity,
    private val laborActualEntity: List<LaborActualEntity>

) :
    RecyclerView.Adapter<LaborActualAdapter.ViewHolder>() {
    val TAG = LaborActualAdapter::class.java.name


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
        fun bind(laborActualEntity: LaborActualEntity) {
            val vendor = StringUtils.NVL(laborActualEntity.vendor, BaseParam.APP_DASH)
            val laborcode = StringUtils.NVL(laborActualEntity.laborcode, BaseParam.APP_DASH)
            val craft = StringUtils.NVL(laborActualEntity.craft, BaseParam.APP_DASH)
            val skillLevel = StringUtils.NVL(laborActualEntity.skillLevel, BaseParam.APP_DASH)
            binding.apply {
                tvType.text = laborActualEntity.taskid.plus(BaseParam.APP_DASH)
                    .plus(laborActualEntity.taskDescription)
                tvStatus.text = vendor
                tvLabor.text = laborcode
                tvCraft.text = craft
                tvSkillLevel.text = skillLevel
                cardLaborPlan.setOnClickListener {
                    val intent = Intent(activity, LaborActualDetailsActivity ::class.java)
                    intent.putExtra(BaseParam.WONUM, laborActualEntity.wonumHeader)
                    intent.putExtra(BaseParam.WORKORDERID, laborActualEntity.workorderid)
                    intent.putExtra(BaseParam.LABORCODE, laborActualEntity.laborcode)
                    intent.putExtra(BaseParam.CRAFT, laborActualEntity.craft)
                    intent.putExtra(BaseParam.OBJECTBOXID, laborActualEntity.id)
                    activity.startActivity(intent)
                    activity.finish()
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laborActualEntity: LaborActualEntity = laborActualEntity[position]
        holder.bind(laborActualEntity)
    }


    override fun getItemCount(): Int {
        return laborActualEntity.size
    }
}

