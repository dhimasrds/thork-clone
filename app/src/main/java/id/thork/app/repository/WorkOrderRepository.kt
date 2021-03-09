package id.thork.app.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.utils.WoUtils
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderRepository @Inject constructor(
    private val workOrderClient: WorkOrderClient,
    private val woCacheDao: WoCacheDao,
    private val appSession: AppSession
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
    ) {
        val response = workOrderClient.getWorkOrderList(
            headerParam,
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

    fun getWoList(
        appSession: AppSession,
        workOrderRepository: WorkOrderRepository,
    ) =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WoPagingSource(
                    appSession = appSession,
                    repository = workOrderRepository,
                    woCacheDao,
                    null
                )
            }
        ).liveData

    fun getSearchWo(
        appSession: AppSession,
        workOrderRepository: WorkOrderRepository,
        query: String
    ) =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WoPagingSource(
                    appSession = appSession,
                    repository = workOrderRepository,
                    woCacheDao,
                    query
                )
            }
        ).liveData

    fun fetchWoList(): List<WoCacheEntity> {
        return woCacheDao.findAllWo()
    }

    fun findWobyWonum(wonum: String): WoCacheEntity? {
        val woCacheEntity = woCacheDao.findWoByWonum(wonum)
        woCacheEntity.whatIfNotNull { return woCacheEntity }
        return null
    }

    fun updateWo(woCacheEntity: WoCacheEntity, username: String){
        return woCacheDao.updateWo(woCacheEntity, username)
    }

    fun addWoToObjectBox(list: List<Member>) {
        for (wo in list) {
            val woCacheEntity = WoCacheEntity(
                syncBody = WoUtils.convertMemberToBody(wo),
                woId = wo.workorderid,
                wonum = wo.wonum,
                status = wo.status,
                isChanged = BaseParam.APP_TRUE,
                isLatest = BaseParam.APP_TRUE,
                syncStatus = BaseParam.APP_TRUE,
                laborCode = wo.cxlabor
            )
            setupWoLocation(woCacheEntity, wo)
            woCacheEntity.createdDate = Date()
            woCacheEntity.createdBy = appSession.userEntity.username
            woCacheEntity.updatedBy = appSession.userEntity.username
            saveWoList(woCacheEntity, appSession.userEntity.username)
        }
    }


    private fun setupWoLocation(woCacheEntity: WoCacheEntity, wo: Member) {
        woCacheEntity.latitude = if (!wo.woserviceaddress.isNullOrEmpty()) {
            wo.woserviceaddress[0].latitudey
        } else {
            null
        }
        woCacheEntity.longitude = if (!wo.woserviceaddress.isNullOrEmpty()) {
            wo.woserviceaddress[0].longitudex
        } else {
            null
        }
    }


}