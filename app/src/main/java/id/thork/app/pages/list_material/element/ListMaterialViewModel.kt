package id.thork.app.pages.list_material.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.MaterialRepository

/**
 * Created by Raka Putra on 3/2/21
 * Jakarta, Indonesia.
 */
class ListMaterialViewModel @ViewModelInject constructor(private val repository: MaterialRepository) :
    LiveCoroutinesViewModel() {

    fun fetchMaterialList(workorderId: Int): List<MaterialEntity?>? {
        val material: List<MaterialEntity?>? = repository.listMaterials(workorderId)
        val exist = material
        return exist
    }

    fun fetchMaterialListByWonum(wonum: String): List<MaterialEntity?>? {
        val material: List<MaterialEntity?>? = repository.listMaterialsByWonum(wonum)
        val exist = material
        return exist
    }

    fun saveListMaterial(currentDate: String, currentTime: String, result: String?, woId: Int) {
        val materialEntity = MaterialEntity()
        materialEntity.time = currentTime
        materialEntity.date = currentDate
        materialEntity.resultCode = result
        materialEntity.workorderId = woId
        repository.saveMaterial(materialEntity)
    }

    fun saveListMaterialByWonum(currentDate: String, currentTime: String, result: String?, wonum: String) {
        val materialEntity = MaterialEntity()
        materialEntity.time = currentTime
        materialEntity.date = currentDate
        materialEntity.resultCode = result
        materialEntity.wonum = wonum
        repository.saveMaterial(materialEntity)
    }
}