package id.thork.app.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.entity.WoCacheEntity
import timber.log.Timber

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
        onError: (String) -> Unit,
        onException: (String) -> Unit
    )  {
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
                onException(message())
            }

    }

    fun saveWoList(woCacheEntity: WoCacheEntity, username: String?): WoCacheEntity{
        return woCacheDao.createWoCache(woCacheEntity, username)
    }

    fun fetchWoList(): List<WoCacheEntity>{
        return woCacheDao.findAllWo()
    }

    fun findWobyWonum(wonum: String): WoCacheEntity? {
        return woCacheDao.findWoByWonum(wonum)
    }
}