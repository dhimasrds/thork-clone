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
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MaterialActualEntity
import id.thork.app.repository.MaterialActualRepository
import id.thork.app.repository.MaterialRepository

class MaterialActualViewModel@ViewModelInject constructor(
    private val context: Context,
    private val materialActualRepository: MaterialActualRepository
) : LiveCoroutinesViewModel() {
    val TAG = MaterialActualViewModel::class.java.name

    private val _listMaterial = MutableLiveData<List<MaterialActualEntity>>()
    private val _result = MutableLiveData<Int>()

    val listMaterial: LiveData<List<MaterialActualEntity>> get() = _listMaterial
    val result : LiveData<Int> get() = _result

    fun initListMaterialActual(workorderid: String) {
        val materialPlan = materialActualRepository.findListMaterialActualByWoid(workorderid.toInt())
        materialPlan.whatIfNotNull {
            _listMaterial.value = it
        }
    }

}