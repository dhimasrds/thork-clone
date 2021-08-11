package id.thork.app.repository

import id.thork.app.base.BaseRepository
import id.thork.app.persistence.dao.SysPropDao
import id.thork.app.persistence.entity.SysPropEntity

/**
 * Created by M.Reza Sulaiman on 29/04/21
 * Jakarta, Indonesia.
 */
class SystemPropertiesRepository constructor(
    private val sysPropDao: SysPropDao
) : BaseRepository() {
    val TAG = SystemPropertiesRepository::class.java.name

    fun findAppKey(propertiesKey: String): SysPropEntity? {
        return sysPropDao.findBypropertiesKey(propertiesKey)
    }

    fun findApiKeyGoogleMaps() {

    }


}