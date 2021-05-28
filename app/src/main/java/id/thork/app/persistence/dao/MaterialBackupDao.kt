package id.thork.app.persistence.dao

import id.thork.app.persistence.entity.MaterialBackupEntity

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
interface MaterialBackupDao {
    fun saveMaterial(materialBackupEntity: MaterialBackupEntity): MaterialBackupEntity?

    fun saveMaterialList(materialBackupEntity: List<MaterialBackupEntity?>?): List<MaterialBackupEntity?>?

    fun listMaterials(workorderId: Int): List<MaterialBackupEntity?>?

    fun listMaterialsByWonum(wonum: String): List<MaterialBackupEntity?>?

    fun listMaterialsByWoid(woid: Int): List<MaterialBackupEntity?>?

    fun removeMaterialByWonum(wonum: String): Long

    fun removeMaterialByWoid(woid: Int): Long

}