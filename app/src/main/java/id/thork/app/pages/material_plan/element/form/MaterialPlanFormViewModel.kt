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
import id.thork.app.helper.builder.model.LocomotifAttribute
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.WpmaterialEntity
import id.thork.app.repository.MaterialRepository
import id.thork.app.repository.WpMaterialRepository
import timber.log.Timber

class MaterialPlanFormViewModel @ViewModelInject constructor(
    private val context: Context,
    private val materialRepository: MaterialRepository,
    private val wpMaterialRepository: WpMaterialRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    private val TAG = MaterialPlanFormViewModel::class.java.name

    private var currentWpMaterial: WpmaterialEntity? = null

    private val _materialItems = MutableLiveData<MutableList<LocomotifAttribute>>()
    val materialItems: LiveData<MutableList<LocomotifAttribute>> get() = _materialItems

    private val _storeroomItems = MutableLiveData<MutableList<LocomotifAttribute>>()
    val storeroomItems: LiveData<MutableList<LocomotifAttribute>> get() = _storeroomItems

    private val _lineTypeItems = MutableLiveData<List<LocomotifAttribute>>()
    val lineTypeItems: LiveData<List<LocomotifAttribute>> get() = _lineTypeItems

    private val _wpMaterialCache = MutableLiveData<WpmaterialEntity>()
    val wpMaterialCache: LiveData<WpmaterialEntity> get() = _wpMaterialCache

    private val _materialItem = MutableLiveData<MaterialEntity>()
    val materialItem: LiveData<MaterialEntity> get() = _materialItem

    private val _result = MutableLiveData<Int>()
    val result: LiveData<Int> get() = _result

    fun setupEntity(formState: String, woId: Int, id: Long) {
        Timber.tag(TAG).d("setupEntity() formState: %s woId: %s", formState, woId)
        if (formState.equals(BaseParam.FORM_STATE_NEW)) {
            currentWpMaterial = WpmaterialEntity()
            currentWpMaterial?.workorderId = woId
            currentWpMaterial?.itemQty = 1
            currentWpMaterial?.siteid = appSession.userEntity.siteid
            currentWpMaterial?.orgid = appSession.userEntity.orgid
            _wpMaterialCache.value = currentWpMaterial
        } else if (formState.equals(BaseParam.FORM_STATE_EDIT)
            || formState.equals(BaseParam.FORM_STATE_READ_ONLY)) {
            currentWpMaterial = wpMaterialRepository.getMaterialPlanByIdAndWoId(id, woId)
            _wpMaterialCache.value = currentWpMaterial
        }
    }

    fun getLineTypeItems() {
        val mutableList = mutableListOf<LocomotifAttribute>()
        val itemType = LocomotifAttribute("ITEM", "ITEM")
        val materialType = LocomotifAttribute("MATERIAL", "MATERIAL")
        mutableList.add(itemType)
        mutableList.add(materialType)
        _lineTypeItems.value = mutableList.toList()
    }

    fun getStoreroomItems() {
        val mutableList = mutableListOf<LocomotifAttribute>()
        val storeroomA = LocomotifAttribute("STOREROOMGST", "STOREROOMGST")
        val storeroomB = LocomotifAttribute("STOREROOMKBN", "STOREROOMKBN")
        val storeroomC = LocomotifAttribute("STOREROOMPJG", "STOREROOMPJG")
        mutableList.add(storeroomA)
        mutableList.add(storeroomB)
        mutableList.add(storeroomC)
        _storeroomItems.value = mutableList
    }

    fun getMaterialItems() {
        val mutableList = mutableListOf<LocomotifAttribute>()
        val materialEntities = materialRepository.getListItemMaster()
        materialEntities.whatIfNotNull {
            for (material in it) {
                mutableList.add(LocomotifAttribute(name = material.description, value = material.itemNum))
            }
        }
        _materialItems.value = mutableList
    }

    fun saveMaterialCache(wpmaterialEntity: WpmaterialEntity) {
        wpMaterialRepository.saveMaterialPlan(
            wpmaterialEntity,
            appSession.userEntity.username.toString()
        )
        _result.value = BaseParam.APP_TRUE
    }

    fun deleteMaterialCache(wpmaterialEntity: WpmaterialEntity) {
        wpMaterialRepository.deleteMaterialPlan(wpmaterialEntity)
        _result.value = BaseParam.APP_TRUE
    }

    fun getItemNumResult(itemnum: String) {
        val materialCache = materialRepository.getListByItemnum(itemnum)
        materialCache.whatIfNotNull {
            _materialItem.value = it
        }
    }

}