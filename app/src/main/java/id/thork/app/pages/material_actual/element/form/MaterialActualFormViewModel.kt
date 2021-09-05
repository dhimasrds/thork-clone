package id.thork.app.pages.material_actual.element.form

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.helper.builder.model.LocomotifAttribute
import id.thork.app.pages.material_plan.element.form.MaterialPlanFormViewModel
import id.thork.app.persistence.entity.MaterialActualEntity
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.MatusetransEntity
import id.thork.app.persistence.entity.WpmaterialEntity
import id.thork.app.repository.*
import timber.log.Timber

/**
 * Created by Reja on 05/09/21
 * Jakarta, Indonesia.
 */
class MaterialActualFormViewModel @ViewModelInject constructor(
    private val context: Context,
    private val materialRepository: MaterialRepository,
    private val storeroomRepository: StoreroomRepository,
    private val materialStoreroomRepository: MaterialStoreroomRepository,
    private val materialActualRepository: MaterialActualRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {
    private val TAG = MaterialPlanFormViewModel::class.java.name

    private var currentMaterialActual: MaterialActualEntity? = null

    private val _materialItems = MutableLiveData<MutableList<LocomotifAttribute>>()
    val materialItems: LiveData<MutableList<LocomotifAttribute>> get() = _materialItems

    private val _storeroomItems = MutableLiveData<MutableList<LocomotifAttribute>>()
    val storeroomItems: LiveData<MutableList<LocomotifAttribute>> get() = _storeroomItems

    private val _lineTypeItems = MutableLiveData<List<LocomotifAttribute>>()
    val lineTypeItems: LiveData<List<LocomotifAttribute>> get() = _lineTypeItems

    private val _materialActualCache = MutableLiveData<MaterialActualEntity>()
    val materialActualCache: LiveData<MaterialActualEntity> get() = _materialActualCache

    private val _materialItem = MutableLiveData<MaterialEntity>()
    val materialItem: LiveData<MaterialEntity> get() = _materialItem

    private val _result = MutableLiveData<Int>()
    val result: LiveData<Int> get() = _result

    fun setupEntity(formState: String, woId: Int, id: Long) {
        Timber.tag(TAG).d("setupEntity() formState: %s woId: %s", formState, woId)
        if (formState.equals(BaseParam.FORM_STATE_NEW)) {
            currentMaterialActual = MaterialActualEntity()
            currentMaterialActual?.workorderId = woId
            currentMaterialActual?.itemQty = 1
            currentMaterialActual?.siteid = appSession.userEntity.siteid
            currentMaterialActual?.orgid = appSession.userEntity.orgid
            _materialActualCache.value = currentMaterialActual
        } else if (formState.equals(BaseParam.FORM_STATE_EDIT)
            || formState.equals(BaseParam.FORM_STATE_READ_ONLY)) {
            currentMaterialActual = materialActualRepository.getMaterialActualByIdAndWoId(id, woId)
            _materialActualCache.value = currentMaterialActual
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
        mutableList.add(storeroomA)
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

    fun getStoreroomsByItem(itemnum: String) {
        val mutableList = mutableListOf<LocomotifAttribute>()
        val materialStoreroomEntities = materialStoreroomRepository.getStoreroomByItemNum(itemnum)
        materialStoreroomEntities.whatIfNotNull { matStorerooms ->
            for (matStoreroom in matStorerooms) {
                matStoreroom.location.whatIfNotNull { location ->
                    val listStoreroom = storeroomRepository.getStoreroom(location)
                    for (storeroom in listStoreroom) {
                        mutableList.add(LocomotifAttribute(storeroom.description, storeroom.location))
                    }
                }
            }
        }
        _storeroomItems.value = mutableList
    }

    fun saveMaterialCache(materialActualEntity: MaterialActualEntity) {
        materialActualRepository.saveMaterialActual(
            materialActualEntity,
            appSession.userEntity.username.toString()
        )
        _result.value = BaseParam.APP_TRUE
    }

    fun deleteMaterialCache(materialActualEntity: MaterialActualEntity) {
        materialActualRepository.deleteMaterialActual(materialActualEntity)
        _result.value = BaseParam.APP_TRUE
    }

    fun getItemNumResult(itemnum: String) {
        val materialCache = materialRepository.getListByItemnum(itemnum)
        materialCache.whatIfNotNull {
            _materialItem.value = it
        }
    }

}