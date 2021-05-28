package id.thork.app.repository

import id.thork.app.base.BaseRepository
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.persistence.dao.MaterialDao
import id.thork.app.persistence.entity.MaterialBackupEntity
import javax.inject.Inject

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
class MaterialRepository @Inject constructor(
    private val workOrderClient: WorkOrderClient,
    private val materialDao: MaterialDao) : BaseRepository {

    fun saveMaterial(materialBackupEntity: MaterialBackupEntity): MaterialBackupEntity? {
        return materialDao.saveMaterial(materialBackupEntity)
    }

    fun saveMaterialList(materialBackupEntity: List<MaterialBackupEntity?>): List<MaterialBackupEntity?>? {
        return materialDao.saveMaterialList(materialBackupEntity)
    }

    fun listMaterials(workorderId: Int): List<MaterialBackupEntity?>? {
        return materialDao.listMaterials(workorderId)
    }

    fun listMaterialsByWonum(wonum: String): List<MaterialBackupEntity?>? {
        return materialDao.listMaterialsByWonum(wonum)
    }

    fun listMaterialsByWoid(woid: Int): List<MaterialBackupEntity?>? {
        return materialDao.listMaterialsByWoid(woid)
    }

    fun removeMaterialByWonum(wonum: String): Long{
        return materialDao.removeMaterialByWonum(wonum)
    }

    fun removeMaterialByWoid(woid: Int): Long{
        return materialDao.removeMaterialByWoid(woid)
    }
}