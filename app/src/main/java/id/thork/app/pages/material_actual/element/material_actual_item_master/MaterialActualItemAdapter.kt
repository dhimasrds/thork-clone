package id.thork.app.pages.material_actual.element.material_actual_item_master

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.databinding.MaterialPlanItemBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.material_actual.element.form.MaterialActualFormActivity
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.utils.StringUtils
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 30/05/2021
 * Jakarta, Indonesia.
 */
class MaterialActualItemAdapter constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val requestOptions: RequestOptions,
    private val materialEntities: List<MaterialEntity>,
    private val activity: MaterialActualItem
) : RecyclerView.Adapter<MaterialActualItemAdapter.MaterialPlanHolder>(), Filterable {
    val TAG = MaterialActualItemAdapter::class.java.name
    var materialEntityListFilter = ArrayList<MaterialEntity>()

    lateinit var materialEntity: MaterialEntity

    init {
        materialEntityListFilter = materialEntities as ArrayList<MaterialEntity>

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialPlanHolder {

        return MaterialPlanHolder(
            MaterialPlanItemBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MaterialPlanHolder, position: Int) {
        val materialEntity: MaterialEntity = materialEntityListFilter[position]
        holder.bind(materialEntity, activity)
    }

    override fun getItemCount(): Int = materialEntityListFilter.size

    inner class MaterialPlanHolder(val binding: MaterialPlanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(materialEntity: MaterialEntity, activity: MaterialActualItem) {
            with(binding) {
                tvItemNum.text = StringUtils.truncate(materialEntity.itemNum, 30)
                tvItemType.text = StringUtils.truncate(materialEntity.itemType, 30)
                tvDescription.text = StringUtils.truncate(materialEntity.description, 30)

                root.setOnClickListener {
                    val intent =
                        Intent(BaseApplication.context, MaterialActualFormActivity::class.java)
                    intent.putExtra(BaseParam.MATERIAL, materialEntity.itemNum)
                    intent.putExtra(BaseParam.DESCRIPTION, materialEntity.description)
                    activity.setResult(AppCompatActivity.RESULT_OK, intent)
                    activity.finish()
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    materialEntityListFilter =
                        materialEntities as ArrayList<MaterialEntity>
                } else {
                    Timber.d("filter result :%s", materialEntities.size)
                    val resultList = ArrayList<MaterialEntity>()
                    for (entity in materialEntities) {
                        if (entity.itemNum?.toLowerCase()
                                ?.contains(charSearch.toLowerCase()) == true || entity.description?.toLowerCase()
                                ?.contains(charSearch.toLowerCase()) == true
                        ) {

                            Timber.d("filter result text:%s", entity.itemNum)
                            resultList.add(entity)
                        }
                    }
                    materialEntityListFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = materialEntityListFilter
                Timber.d("filter result :%s", filterResults)
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                materialEntityListFilter = results?.values as ArrayList<MaterialEntity>
                notifyDataSetChanged()
            }
        }
    }
}
