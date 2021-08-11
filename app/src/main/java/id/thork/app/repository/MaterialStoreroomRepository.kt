package id.thork.app.repository

import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.material_response.Member
import id.thork.app.network.response.work_order.Matusetran
import id.thork.app.network.response.work_order.Wpmaterial
import id.thork.app.persistence.dao.*
import id.thork.app.persistence.entity.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Reja on 11/08/2021
 * Jakarta, Indonesia.
 */
class MaterialStoreroomRepository @Inject constructor(
    private val materialStoreroomDao: MaterialStoreroomDao,
    private val appSession: AppSession
) : BaseRepository() {

    var username: String = ""

    init {
        appSession.whatIfNotNull {
            this.username = it.userEntity.username.toString()
        }
    }

    fun save(materialStoreroomEntity: MaterialStoreroomEntity)  {
        return materialStoreroomDao.save(materialStoreroomEntity, username)
    }

    fun getStoreroomByItemNum(itemNum: String): List<MaterialStoreroomEntity> {
        return materialStoreroomDao.findStorerooms(itemNum)
    }

}