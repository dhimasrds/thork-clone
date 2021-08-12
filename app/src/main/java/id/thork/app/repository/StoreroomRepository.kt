package id.thork.app.repository

import android.content.Context
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.helper.CookieHelper
import id.thork.app.network.api.StoreroomClient
import id.thork.app.network.response.storeroom_response.Inventory
import id.thork.app.network.response.storeroom_response.Member
import id.thork.app.network.response.storeroom_response.StoreroomResponse
import id.thork.app.persistence.dao.MaterialStoreroomDao
import id.thork.app.persistence.dao.StoreroomDao
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.MaterialStoreroomEntity
import id.thork.app.persistence.entity.StoreroomEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Reja on 11/08/2021
 * Jakarta, Indonesia.
 */
class StoreroomRepository @Inject constructor(
    private val context: Context,
    private val storeroomClient: StoreroomClient,
    private val storeroomDao: StoreroomDao,
    private val materialStoreroomDao: MaterialStoreroomDao,
    private val appSession: AppSession
) : BaseRepository() {
    val TAG = StoreroomRepository::class.java.name

    var username: String = ""

    init {
        appSession.whatIfNotNull {
            this.username = it.userEntity.username.toString()
        }
    }

    fun save(storeroomEntity: StoreroomEntity) {
        return storeroomDao.save(storeroomEntity, username)
    }

    fun getStoreroom(location: String): List<StoreroomEntity> {
        return storeroomDao.findStoreroom(location)
    }

    fun addStoreroomCache(members: List<Member>) {
        storeroomDao.delete()
        materialStoreroomDao.delete()
        val storeroomCaches = mutableListOf<StoreroomEntity>()
        val matStoreroomCaches = mutableListOf<MaterialStoreroomEntity>()

        members.whatIfNotNull { it ->
            for (member in it) {
                val storeroomEntity = StoreroomEntity(location = member.location,
                description = member.description, siteid =  member.siteid,
                    orgid = member.orgid, status =  member.status)
                storeroomCaches.add(storeroomEntity)

                member.inventory.whatIfNotNull { itInventory ->
                    for (inventory in itInventory) {
                        member.location.whatIfNotNull { itLocation ->
                            matStoreroomCaches.add(addMaterialStoreroomRelation(inventory, itLocation))
                        }
                    }
                }
            }
            storeroomDao.save(storeroomCaches.toList(), username)
            materialStoreroomDao.save(matStoreroomCaches, username)
        }
    }

    fun addMaterialStoreroomRelation(inventory: Inventory, location: String): MaterialStoreroomEntity {
        return MaterialStoreroomEntity(
            itemNum = inventory.itemnum,
            location = location
        )
    }

    suspend fun fetchStoreroom(
        onSuccess: (StoreroomResponse) -> Unit,
        onError: (String) -> Unit,
        onException: (String) -> Unit,
    ) {
        var maxAuth = BaseParam.APP_EMPTY_STRING
        appSession.userEntity.userHash.whatIfNotNull {
            maxAuth = it
        }
        val cookie = CookieHelper(context, maxAuth).generateCookieIfExpired()
        val response = storeroomClient.getStorerooms(cookie)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                Timber.tag(TAG).i(
                    "fetchStoreroom() response: %s", response
                )
                onSuccess(response)
            }
        }
            .onError {
                Timber.tag(TAG).i(
                    "fetchStoreroom() code: %s error: %s",
                    statusCode.code,
                    message()
                )
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("fetchStoreroom() exception: %s", message())
                onException(message())
            }
    }

}