/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */

package id.thork.app.pages.material_actual.element

import android.content.Context
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import id.thork.app.R
import id.thork.app.databinding.MaterialActualItemBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.persistence.entity.MaterialActualEntity
import id.thork.app.utils.StringUtils
import timber.log.Timber
import java.util.*

class MaterialActualAdapter constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val requestOptions: RequestOptions,
    private val materialActualEntityList: List<MaterialActualEntity>,
    internal val materialActualAdapterItemClickListener: MaterialActualAdapterItemClickListener
) : RecyclerView.Adapter<MaterialActualAdapter.MaterialActualHolder>(), Filterable {
    val TAG = MaterialActualAdapter::class.java.name
    lateinit var materialActualEntity: MaterialActualEntity
    var materialActualEntityListFilter = ArrayList<MaterialActualEntity>()

    private lateinit var customDialogUtils: CustomDialogUtils
    var currentItemnum: String? = null

    init {
        materialActualEntityListFilter = materialActualEntityList as ArrayList<MaterialActualEntity>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialActualHolder {
        val binding = DataBindingUtil.inflate<MaterialActualItemBinding>(
            from(parent.getContext()),
            R.layout.material_actual_item, parent, false
        )
        return MaterialActualHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialActualHolder, position: Int) {
        val materialActualEntity: MaterialActualEntity = materialActualEntityList[position]
        holder.bind(materialActualEntity)
    }

    override fun getItemCount(): Int = materialActualEntityListFilter.size

    inner class MaterialActualHolder(val binding: MaterialActualItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(materialActualEntity: MaterialActualEntity) {
            with(binding) {
                tvItemNum.text = StringUtils.truncate(materialActualEntity.itemNum, 30)
                tvItemType.text = StringUtils.truncate(materialActualEntity.lineType, 30)
                tvDescription.text = StringUtils.truncate(materialActualEntity.description, 30)
            }
        }

        override fun onClick(view: View?) {
            materialActualAdapterItemClickListener.onClickItem(materialActualEntityList[this.bindingAdapterPosition])
        }
    }


    interface MaterialActualAdapterItemClickListener {
        fun onClickItem(materialActualEntity: MaterialActualEntity)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    materialActualEntityListFilter =
                        materialActualEntityList as ArrayList<MaterialActualEntity>
                } else {
                    Timber.d("filter result :%s", materialActualEntityList.size)
                    val resultList = ArrayList<MaterialActualEntity>()
                    for (entity in materialActualEntityList) {
                        if (entity.itemNum?.toLowerCase()
                                ?.contains(charSearch.toLowerCase()) == true || entity.description?.toLowerCase()
                                ?.contains(charSearch.toLowerCase()) == true
                        ) {

                            Timber.d("filter result text:%s", entity.itemNum)
                            resultList.add(entity)
                        }
                    }
                    materialActualEntityListFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = materialActualEntityListFilter
                Timber.d("filter result :%s", filterResults)
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                materialActualEntityListFilter = results?.values as ArrayList<MaterialActualEntity>
                notifyDataSetChanged()
            }
        }
    }
}