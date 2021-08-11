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
import id.thork.app.persistence.entity.CraftMasterEntity
import id.thork.app.utils.StringUtils

/**
 * Created by Dhimas Saputra on 23/06/21
 * Jakarta, Indonesia.
 */
class CraftAdapter constructor(
    private val activity : Activity,
    private val craftMasterEntities: List<CraftMasterEntity>
) :
    RecyclerView.Adapter<CraftAdapter.ViewHolder>() {
    val TAG = LaborPlanAdapter::class.java.name


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
        fun bind(craftMasterEntity: CraftMasterEntity) {
            val craft = craftMasterEntity.craft
            val skill = StringUtils.NVL(craftMasterEntity.skillLevel, BaseParam.APP_EMPTY_STRING)
            binding.apply {
                tvLaborOrCraft.text = craft.plus(BaseParam.APP_WHITESPACE).plus(skill)
                cardLaborPlan.setOnClickListener {
                    val intent = Intent(BaseApplication.context, CreateLaborPlanActivity::class.java)
                    intent.putExtra(BaseParam.CRAFT_FORM, craft)
                    intent.putExtra(BaseParam.SKILL_FORM, skill)
                    activity.setResult(AppCompatActivity.RESULT_OK, intent)
                    activity.finish()
                }

            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val craftCache = craftMasterEntities[position]
        holder.bind(craftCache)
    }

    override fun getItemCount(): Int {
        return craftMasterEntities.size
    }
}


