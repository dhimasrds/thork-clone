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
import id.thork.app.persistence.dao.MaterialBackupDao
import id.thork.app.persistence.dao.MaterialDao
import id.thork.app.persistence.dao.MatusetransDao
import id.thork.app.persistence.dao.WpmaterialDao
import id.thork.app.persistence.entity.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by Reja on 8/8/21
 * Jakarta, Indonesia.
 */
class WpMaterialRepository @Inject constructor(
    private val wpmaterialDao: WpmaterialDao,
    private val appSession: AppSession
) : BaseRepository() {
    private val TAG = WpMaterialRepository::class.java.name

    /**
     * Material Plan
     */
    fun saveMaterialPlan(wpmaterialEntity: WpmaterialEntity, username: String) {
        wpmaterialDao.save(wpmaterialEntity, username)
    }

    fun deleteMaterialPlan(wpmaterialEntity: WpmaterialEntity) {
        wpmaterialDao.delete(wpmaterialEntity)
    }

    fun getMaterialPlanByIdAndWoId(id: Long, woId: Int): WpmaterialEntity? {
        return wpmaterialDao.findByIdAndWoId(id, woId)
    }

    fun findListMaterialActualByWoid(woId: Int): List<WpmaterialEntity> {
        return wpmaterialDao.findListMaterialActualByWoid(woId)
    }
}