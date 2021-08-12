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
import id.thork.app.pages.labor_plan.details_labor_plan.LaborPlanDetailsActivity
import id.thork.app.persistence.entity.LaborMasterEntity

/**
 * Created by Dhimas Saputra on 23/06/21
 * Jakarta, Indonesia.
 */
class LaborAdapter constructor(
    private val activity: Activity,
    private val laborMasterEntity: List<LaborMasterEntity>,
    private val intentTag: String

) :
    RecyclerView.Adapter<LaborAdapter.ViewHolder>() {
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
        fun bind(laborMasterEntity: LaborMasterEntity) {
            binding.apply {
                tvLaborOrCraft.text = laborMasterEntity.laborcode
                when (intentTag) {
                    BaseParam.APP_CREATE -> {
                        cardLaborPlan.setOnClickListener {
                            val intent =
                                Intent(BaseApplication.context, CreateLaborPlanActivity::class.java)
                            intent.putExtra(BaseParam.LABORCODE_FORM, laborMasterEntity.laborcode)
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
                            intent.putExtra(BaseParam.LABORCODE_FORM, laborMasterEntity.laborcode)
                            activity.setResult(AppCompatActivity.RESULT_OK, intent)
                            activity.finish()
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laborMasterEntity: LaborMasterEntity = laborMasterEntity[position]
        holder.bind(laborMasterEntity)
    }

    override fun getItemCount(): Int {
        return laborMasterEntity.size
    }
}


