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
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.pages.material_plan.element.MaterialPlanViewModel
import id.thork.app.persistence.entity.MatusetransEntity
import id.thork.app.persistence.entity.WpmaterialEntity
import id.thork.app.repository.MaterialRepository

class MaterialActualViewModel@ViewModelInject constructor(
    private val context: Context,
    private val materialRepository: MaterialRepository
) : LiveCoroutinesViewModel() {
    val TAG = MaterialPlanViewModel::class.java.name

    private val _listMaterial = MutableLiveData<List<MatusetransEntity>>()

    val listMaterial: LiveData<List<MatusetransEntity>> get() = _listMaterial


    fun initListMaterialActual(workorderid: String) {
        val materialPlan = materialRepository.getListMaterialActualByWoid(workorderid)
        materialPlan.whatIfNotNull {
            _listMaterial.value = it
        }
    }

}