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

package id.thork.app.pages.material_plan.element.material_plan_list_item_master

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.repository.MaterialRepository

class MaterialPlanItemViewModel @ViewModelInject constructor(
    private val context: Context,
    private val materialRepository: MaterialRepository
) : LiveCoroutinesViewModel() {
    val TAG = MaterialPlanItemViewModel::class.java.name

    private val _listMaterial = MutableLiveData<List<MaterialEntity>>()

    val listMaterial: LiveData<List<MaterialEntity>> get() = _listMaterial


    fun initListMaterial() {
        val materialPlan = materialRepository.getListItemMaster()
        materialPlan.whatIfNotNull {
            _listMaterial.value = it
        }
    }

}