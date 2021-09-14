package id.thork.app.pages.labor_actual.element

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
import id.thork.app.databinding.CardviewLaborCraftBinding
import id.thork.app.pages.labor_actual.create_labor_actual.CreateLaborActualActivity
import id.thork.app.pages.labor_plan.create_labor_plan.CreateLaborPlanActivity
import id.thork.app.pages.labor_plan.details_labor_plan.LaborPlanDetailsActivity
import id.thork.app.pages.labor_plan.element.LaborAdapter
import id.thork.app.pages.labor_plan.element.LaborPlanAdapter
import id.thork.app.persistence.entity.CraftMasterEntity
import id.thork.app.persistence.entity.LaborMasterEntity
import id.thork.app.utils.StringUtils

/**
 * Created by Dhimas Saputra on 08/09/21
 * Jakarta, Indonesia.
 */
class SelectLaborAndCraftAdapter constructor(
    private val activity: Activity,
    private val craftMasterEntity: List<CraftMasterEntity>,
    private val intentTag: String

) :
    RecyclerView.Adapter<SelectLaborAndCraftAdapter.ViewHolder>() {
    val TAG = LaborPlanAdapter::class.java.name


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardviewLaborCraftBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(val binding: CardviewLaborCraftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(craftMasterEntity: CraftMasterEntity) {
            binding.apply {
                tvLabor.text = craftMasterEntity.laborcode
                tvCraft.text = craftMasterEntity.craft
                tvSkillLevel.text = StringUtils.NVL(craftMasterEntity.skillLevel,BaseParam.APP_DASH)
                when (intentTag) {
                    BaseParam.APP_CREATE -> {
                        cardLaborPlan.setOnClickListener {
                            val intent =
                                Intent(BaseApplication.context, CreateLaborPlanActivity::class.java)
                            intent.putExtra(BaseParam.LABORCODE_FORM, craftMasterEntity.laborcode)
                            activity.setResult(AppCompatActivity.RESULT_OK, intent)
                            activity.finish()
                        }
                    }

                    BaseParam.LABORCODE_FORM_ACTUAL -> {
                        cardLaborPlan.setOnClickListener {
                            val intent =
                                Intent(BaseApplication.context, CreateLaborActualActivity::class.java)
                            intent.putExtra(BaseParam.LABORCODE_FORM, craftMasterEntity.laborcode)
                            intent.putExtra(BaseParam.CRAFT_FORM, craftMasterEntity.craft)
                            intent.putExtra(BaseParam.SKILL_FORM, craftMasterEntity.skillLevel)
                            activity.setResult(AppCompatActivity.RESULT_OK, intent)
                            activity.finish()
                        }
                    }

                    BaseParam.APP_DETAIL -> {
                        cardLaborPlan.setOnClickListener {
                            val intent = Intent(
                                BaseApplication.context,
                                LaborPlanDetailsActivity::class.java
                            )
                            intent.putExtra(BaseParam.LABORCODE_FORM, craftMasterEntity.laborcode)
                            activity.setResult(AppCompatActivity.RESULT_OK, intent)
                            activity.finish()
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val craftMasterEntity: CraftMasterEntity = craftMasterEntity[position]
        holder.bind(craftMasterEntity)
    }

    override fun getItemCount(): Int {
        return craftMasterEntity.size
    }
}


