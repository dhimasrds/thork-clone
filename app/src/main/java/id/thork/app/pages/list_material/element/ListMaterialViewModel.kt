package id.thork.app.pages.list_material.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MaterialBackupEntity
import id.thork.app.repository.MaterialRepository

/**
 * Created by Raka Putra on 3/2/21
 * Jakarta, Indonesia.
 */
class ListMaterialViewModel @ViewModelInject constructor(private val repository: MaterialRepository) :
    LiveCoroutinesViewModel() {

    fun fetchMaterialList(workorderId: Int): List<MaterialBackupEntity?>? {
        val materialBackup: List<MaterialBackupEntity?>? = repository.listMaterials(workorderId)
        val exist = materialBackup
        return exist
    }

    fun fetchMaterialListByWonum(wonum: String): List<MaterialBackupEntity?>? {
        val materialBackup: List<MaterialBackupEntity?>? = repository.listMaterialsByWonum(wonum)
        val exist = materialBackup
        return exist
    }

    fun saveListMaterial(currentDate: String, currentTime: String, result: String?, woId: Int) {
        val materialEntity = MaterialBackupEntity()
        materialEntity.time = currentTime
        materialEntity.date = currentDate
        materialEntity.resultCode = result
        materialEntity.workorderId = woId
        repository.saveMaterial(materialEntity)
    }

    fun saveListMaterialByWonum(currentDate: String, currentTime: String, result: String?, wonum: String) {
        val materialEntity = MaterialBackupEntity()
        materialEntity.time = currentTime
        materialEntity.date = currentDate
        materialEntity.resultCode = result
        materialEntity.wonum = wonum
        repository.saveMaterial(materialEntity)
    }
}