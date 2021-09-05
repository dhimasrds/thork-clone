package id.thork.app.repository

import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.material_response.Member
import id.thork.app.network.response.work_order.Matusetran
import id.thork.app.network.response.work_order.Wpmaterial
import id.thork.app.pages.material_plan.element.form.MaterialPlanFormViewModel
import id.thork.app.persistence.dao.*
import id.thork.app.persistence.entity.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by Reja on 5/9/21
 * Jakarta, Indonesia.
 */
class MaterialActualRepository @Inject constructor(
    private val materialActualDao: MaterialActualDao,
    private val appSession: AppSession
) : BaseRepository() {
    private val TAG = MaterialActualRepository::class.java.name

    /**
     * Material Actual
     */
    fun saveMaterialActual(materialActualEntity: MaterialActualEntity, username: String) {
        materialActualDao.save(materialActualEntity, username)
    }

    fun deleteMaterialActual(materialActualEntity: MaterialActualEntity) {
        materialActualDao.delete(materialActualEntity)
    }

    fun getMaterialActualByIdAndWoId(id: Long, woId: Int): MaterialActualEntity? {
        return materialActualDao.findByIdAndWoId(id, woId)
    }

    fun findListMaterialActualByWoid(woId: Int): List<MaterialActualEntity> {
        return materialActualDao.findListMaterialActualByWoid(woId)
    }
}