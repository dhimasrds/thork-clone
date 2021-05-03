package id.thork.app.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.entity.WoCacheEntity
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 16/02/21
 * Jakarta, Indonesia.
 */
class WoActivityRepository constructor(
    private val workOrderClient: WorkOrderClient,
    private val woCacheDao: WoCacheDao,
) : BaseRepository {
    val TAG = WorkOrderRepository::class.java.name

    suspend fun getWorkOrderList(
        select: String,
        where: String,
        pageno: Int,
        pagesize: Int,
        onSuccess: (WorkOrderResponse) -> Unit,
        onError: (String) -> Unit,
        onException: (String) -> Unit
    ) {
        val response = workOrderClient.getWorkOrderList(
            select,
            where,
            pageno,
            pagesize
        )
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //TODO
                //Save user session into local cache
                onSuccess(response)
                Timber.tag(TAG).i("repository getWorkOrderList() code:%s", response.member)
                Timber.tag(TAG).i("repository getWorkOrderList() code:%s", statusCode.code)
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
                Timber.tag(TAG).i("reposs getWorkOrderList() exception: %s", message())
                onException(message())
            }

    }

    suspend fun searchWorkOrder(
        headerParam: String,
        select: String,
        where: String,
        onSuccess: (WorkOrderResponse) -> Unit,
        onError: (String) -> Unit,
        onException: (String) -> Unit
    ) {
        val response = workOrderClient.searchWorkOrder(
            headerParam,
            select,
            where
        )
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //TODO
                //Save user session into local cache
                onSuccess(response)
                Timber.tag(TAG).i("repository searchWorkOrder() code:%s", response.member)
                Timber.tag(TAG).i("repository searchWorkOrder() code:%s", statusCode.code)
            }
        }
            .onError {
                Timber.tag(TAG).i(
                    "searchWorkOrder() code: %s error: %s",
                    statusCode.code,
                    message()
                )
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("searchWorkOrder() exception: %s", message())
                onException(message())
            }

    }

    fun saveWoList(woCacheEntity: WoCacheEntity, username: String?): WoCacheEntity {
        return woCacheDao.createWoCache(woCacheEntity, username)
    }

    fun findWoByWonumAndStatus(wonum: String, status: String): WoCacheEntity? {
        return woCacheDao.findWoByWonumAndStatus(wonum, status)
    }

    fun getWoList(
        appSession: AppSession,
        repository: WoActivityRepository,
    ) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WoActivityPagingSource(
                    appSession = appSession,
                    repository = repository,
                    woCacheDao,
                    null
                )
            }
        ).liveData

    fun getSearchWo(
        appSession: AppSession,
        repository: WoActivityRepository,
        query: String
    ) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WoActivityPagingSource(
                    appSession = appSession,
                    repository = repository,
                    woCacheDao,
                    query
                )
            }
        ).liveData

}