package id.thork.app.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.entity.WoCacheEntity
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type
import java.util.*

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderRepository constructor(
    private val workOrderClient: WorkOrderClient,
    private val woCacheDao: WoCacheDao,
) : BaseRepository {
    val TAG = WorkOrderRepository::class.java.name

    suspend fun getWorkOrderList(
        headerParam: String,
        select: String,
        where: String,
        pageno: Int,
        pagesize: Int,
        onSuccess: (WorkOrderResponse) -> Unit,
        onError: (String) -> Unit
    ) :WorkOrderResponse {
        val response = workOrderClient.getWorkOrderList(
            headerParam,
            select,
            where,
            pageno,
            pagesize
        )
        response.suspendOnSuccess {
            data.whatIfNotNull { response->
                //TODO
                //Save user session into local cache
                onSuccess(response)
                Timber.tag(TAG).i("getWorkOrderList() code:%s", statusCode.code)
            }
        }
            .onError {
                Timber.tag(TAG).i(
                    "getWorkOrderList() code: %s error: %s",
                    statusCode.code,
                    message()
                )
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("getWorkOrderList() exception: %s", message())
                onError(message())
            }

        return WorkOrderResponse()
    }

    fun saveWoList(woCacheEntity: WoCacheEntity, username: String?): WoCacheEntity{
        return woCacheDao.createWoCache(woCacheEntity, username)
    }

    private fun findWo(): String? {
        val cacheEntities: List<WoCacheEntity> = woCacheDao.findAllWo()
        val body = ArrayList<String>()
        for (i in cacheEntities.indices) {
          if (cacheEntities[i].status !=null && !cacheEntities[i].status.equals(BaseParam.COMPLETED)
              && !cacheEntities[i].status.equals(BaseParam.WAPPR)
             ){
              body.add(cacheEntities[i].syncBody!!)
          }
        }

        Timber.d("json : %s", body.toString())
        return body.toString()
    }

    @Throws(IOException::class)
    fun listWoOffline(): List<Member>? {
        val moshi = Moshi.Builder().build()
        val listMyData: Type = Types.newParameterizedType(
            MutableList::class.java,
            Member::class.java
        )
        val adapter: JsonAdapter<List<Member>> = moshi.adapter<List<Member>>(listMyData)
        return adapter.fromJson(findWo())!!
    }

}