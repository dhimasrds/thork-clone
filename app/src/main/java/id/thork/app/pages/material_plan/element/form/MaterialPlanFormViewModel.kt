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

package id.thork.app.pages.material_plan.element.form

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.pages.rfid_create_wo_material.element.RfidMaterialActivityViewModel
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.WpmaterialEntity
import id.thork.app.repository.MaterialRepository

class MaterialPlanFormViewModel @ViewModelInject constructor(
    private val context: Context,
    private val materialRepository: MaterialRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    private val TAG = MaterialPlanFormViewModel::class.java.name


    private val _materialCache = MutableLiveData<MaterialEntity>()
    val materialCache: LiveData<MaterialEntity> get() = _materialCache

    private val _result = MutableLiveData<Int>()
    val result : LiveData<Int> get() = _result

    fun saveMaterialCache(materialEntity: MaterialEntity, workorderid: String, itemqty: String) {
        val wpmaterialEntity = WpmaterialEntity()
        wpmaterialEntity.itemNum = materialEntity.itemNum
        wpmaterialEntity.description = materialEntity.description
        wpmaterialEntity.itemType = materialEntity.itemType
        wpmaterialEntity.workorderId = workorderid
        wpmaterialEntity.storeroom = materialEntity.storeroom
        wpmaterialEntity.itemqty = itemqty.toInt()
        materialRepository.saveMaterialPlan(wpmaterialEntity, appSession.userEntity.username.toString())
        _result.value = BaseParam.APP_TRUE
    }

    fun checkResultMaterial(itemnum: String) {
        val materialCache = materialRepository.getListByItemnum(itemnum)
        materialCache.whatIfNotNull {
            _materialCache.value = it
        }
    }

}