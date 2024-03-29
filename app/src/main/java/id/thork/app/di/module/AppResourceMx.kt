package id.thork.app.di.module

import android.content.Context
import com.skydoves.whatif.whatIfNotNull
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.persistence.dao.SysResDao
import id.thork.app.persistence.dao.SysResDaoImp
import id.thork.app.persistence.entity.SysResEntity
import id.thork.app.resourceMx.ResourceMxParam
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 07/05/2021
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppResourceMx @Inject constructor(context: Context) {
    val TAG = AppResourceMx::class.java.name

    //TODO hardcode param properties api key for query
    var fsmResItem: String? = null
    var fsmResAsset: String? = null
    var fsmResLocations: String? = null
    var fsmResWorkorder: String? = null
    var fsmResWorkorderHistory: String? = null
    var fsmResAttendace: String? = null
    var sysResDao: SysResDao = SysResDaoImp()

    init {
        reinit()
    }

    fun reinit() {
        val resourceItem: SysResEntity? =
            sysResDao.findBypropertiesKey(ResourceMxParam.FMS_RES_ITEM)
        resourceItem.whatIfNotNull {
            fsmResItem = it.resourcevalue
        }

        val resourceAsset: SysResEntity? =
            sysResDao.findBypropertiesKey(ResourceMxParam.FMS_RES_ASSET)
        resourceAsset.whatIfNotNull {
            fsmResAsset = it.resourcevalue
        }

        val resourceLocations: SysResEntity? =
            sysResDao.findBypropertiesKey(ResourceMxParam.FMS_RES_LOCATIONS)
        resourceLocations.whatIfNotNull {
            fsmResLocations = it.resourcevalue
        }

        val resourceWorkorder: SysResEntity? =
            sysResDao.findBypropertiesKey(ResourceMxParam.FMS_RES_WORKORDER)
        resourceWorkorder.whatIfNotNull {
            fsmResWorkorder = it.resourcevalue
        }

        val resourceWorkorderHistory: SysResEntity? =
            sysResDao.findBypropertiesKey(ResourceMxParam.FMS_RES_WORKORDER_HISTORY)
        resourceWorkorderHistory.whatIfNotNull {
            fsmResWorkorderHistory = it.resourcevalue
        }

        val resourceAttendance: SysResEntity? =
            sysResDao.findBypropertiesKey(ResourceMxParam.FSM_RES_ATTENDANCE)
        resourceAttendance.whatIfNotNull {
            fsmResAttendace = it.resourcevalue
        }
    }

    fun findResourceValue(resourceKey: String): String? {
        val resEntity: SysResEntity? = sysResDao.findBypropertiesKey(resourceKey)
        resEntity.whatIfNotNull(
            whatIf = {
                return it.resourcevalue
            },
            whatIfNot = {
                return null
            }
        )
        return null
    }

}