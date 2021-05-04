package id.thork.app.di.module

import android.content.Context
import com.skydoves.whatif.whatIfNotNull
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.persistence.dao.SysPropDao
import id.thork.app.persistence.dao.SysPropDaoImp
import id.thork.app.persistence.entity.SysPropEntity
import id.thork.app.propertiesMx.PropertiesMxParam
import id.thork.app.utils.StringUtils
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 29/04/21
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppPropertiesMx @Inject constructor(context: Context) {
    val TAG = AppPropertiesMx::class.java.name
    //TODO hardcode param properties api key for query

    var fsmGoogleMapsApikey: String? = null
    var sysPropDao: SysPropDao = SysPropDaoImp()

    init {
        reinitApiKey()
    }

    fun reinitApiKey() {
        val sysPropEntity: SysPropEntity? =
            sysPropDao.findBypropertiesKey(PropertiesMxParam.FSM_APP_GOOGLE_MAP)
        sysPropEntity.whatIfNotNull(
            whatIf = {
                fsmGoogleMapsApikey = StringUtils.decodeToBase64(it.propertiesvalue.toString())
            }
        )
    }

    fun findApikey(propertiesKey: String): String? {
        val sysPropEntity: SysPropEntity? = sysPropDao.findBypropertiesKey(propertiesKey)
        sysPropEntity.whatIfNotNull(
            whatIf = {
                val apiKey: String? = it.propertiesvalue
                val decodeApiKey = StringUtils.decodeToBase64(apiKey.toString())
                return decodeApiKey
            },
            whatIfNot = {
                return null

            }
        )
        return null

    }

}