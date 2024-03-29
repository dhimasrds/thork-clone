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

package id.thork.app.pages.material_plan.element

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
import id.thork.app.databinding.MaterialPlanItemBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.persistence.entity.WpmaterialEntity
import id.thork.app.utils.StringUtils
import timber.log.Timber
import java.util.*


class MaterialPlanAdapter constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val requestOptions: RequestOptions,
    private val wpmaterialEntityList: List<WpmaterialEntity>,
    internal val materialPlanAdapterItemClickListener: MaterialPlanAdapterItemClickListener
) : RecyclerView.Adapter<MaterialPlanAdapter.MaterialPlanHolder>() , Filterable {
    val TAG = MaterialPlanAdapter::class.java.name

    lateinit var wpmaterialEntity: WpmaterialEntity
    var wpmaterialEntityListFilter = ArrayList<WpmaterialEntity>()

    init {
        wpmaterialEntityListFilter = wpmaterialEntityList as ArrayList<WpmaterialEntity>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialPlanHolder {
        val binding = DataBindingUtil.inflate<MaterialPlanItemBinding>(
            from(parent.getContext()),
            R.layout.material_plan_item, parent, false
        )
        return MaterialPlanHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialPlanHolder, position: Int) {
        val wpmaterialEntity: WpmaterialEntity = wpmaterialEntityListFilter[position]
        holder.bind(wpmaterialEntity)
    }

    override fun getItemCount(): Int = wpmaterialEntityListFilter.size

    inner class MaterialPlanHolder(val binding: MaterialPlanItemBinding) :
        RecyclerView.ViewHolder(binding.root),  View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(wpmaterialEntity: WpmaterialEntity) {
            with(binding) {
                tvItemNum.text = StringUtils.truncate(wpmaterialEntity.itemNum, 30)
                tvItemType.text = StringUtils.truncate(wpmaterialEntity.lineType, 30)
                tvDescription.text = StringUtils.truncate(wpmaterialEntity.description, 30)
            }
        }

        override fun onClick(view: View?) {
            materialPlanAdapterItemClickListener.onClickItem(wpmaterialEntityList[this.bindingAdapterPosition])
        }
    }

    interface MaterialPlanAdapterItemClickListener {
        fun onClickItem(wpmaterialEntity: WpmaterialEntity)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    wpmaterialEntityListFilter =
                        wpmaterialEntityList as ArrayList<WpmaterialEntity>
                } else {
                    Timber.d("filter result :%s", wpmaterialEntityList.size)
                    val resultList = ArrayList<WpmaterialEntity>()
                    for (entity in wpmaterialEntityList) {
                        if (entity.itemNum?.toLowerCase()
                                ?.contains(charSearch.toLowerCase()) == true || entity.description?.toLowerCase()
                                ?.contains(charSearch.toLowerCase()) == true
                        ) {

                            Timber.d("filter result text:%s", entity.itemNum)
                            resultList.add(entity)
                        }
                    }
                    wpmaterialEntityListFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = wpmaterialEntityListFilter
                Timber.d("filter result :%s", filterResults)
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                wpmaterialEntityListFilter = results?.values as ArrayList<WpmaterialEntity>
                notifyDataSetChanged()
            }
        }
    }
}