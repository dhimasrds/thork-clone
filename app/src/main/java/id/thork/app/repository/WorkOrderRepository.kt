package id.thork.app.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.response.fsm_location.FsmLocation
import id.thork.app.network.response.work_order.Assignment
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.LocationDao
import id.thork.app.persistence.dao.LocationDaoImp
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.entity.LocationEntity
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
    private val appSession: AppSession,
) : BaseRepository {
    val TAG = WorkOrderRepository::class.java.name
    private val locationDao : LocationDao

    init {
        locationDao = LocationDaoImp()
    }

    suspend fun getWorkOrderList(
        cookie: String,
        savedQuery: String,
        select: String,
        pageno: Int,
        pagesize: Int,
        onSuccess: (WorkOrderResponse) -> Unit,
        onError: (String) -> Unit,
        onException: (String) -> Unit,
    ) {
        val response = workOrderClient.getWorkOrderList(
            cookie, savedQuery, select, pageno, pagesize
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
        onException: (String) -> Unit,
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

    suspend fun createWo(
        headerParam: String,
        body: Member,
        onSuccess: (Member) -> Unit,
        onError: (String) -> Unit,
    ) {
        val response = workOrderClient.createWo(headerParam, body)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                onSuccess(response)
            }
        }.onError {
            Timber.tag(TAG).i("createWo() code: %s error: %s", statusCode.code, message())
            onError(message())
        }
            .onException {
                Timber.tag(TAG).i("createWo() exception: %s", message())
                onError(message())
            }

    }

    suspend fun updateStatus(
        headerParam: String, xMethodeOverride: String, contentType: String,
        workOrderId: Int, body: Member,
        onSuccess: (WorkOrderResponse) -> Unit, onError: (String) -> Unit,
    ) {
        val response =
            workOrderClient.updateStatus(headerParam,
                xMethodeOverride,
                contentType,
                workOrderId,
                body)
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                onSuccess(response)
            }
        }
            .onError {
                Timber.tag(TAG).i("updateStatus() code: %s error: %s", statusCode.code, message())
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("updateStatus() exception: %s", message())
                onError(message())
            }
    }

    fun saveWoList(woCacheEntity: WoCacheEntity, username: String?): WoCacheEntity {
        return woCacheDao.createWoCache(woCacheEntity, username)
    }

    fun saveLocationToLocal(locationEntity: LocationEntity):LocationEntity{
        return locationDao.saveLocation(locationEntity)
    }

    fun deleteLocation(){
        locationDao.deleteLocation()
    }

    fun getWoList(
        appSession: AppSession,
        workOrderRepository: WorkOrderRepository,
        preferenceManager: PreferenceManager,
        appResourceMx: AppResourceMx
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
                    null,
                    preferenceManager,
                    appResourceMx
                )
            }
        ).liveData

    fun getSearchWo(
        appSession: AppSession,
        workOrderRepository: WorkOrderRepository,
        query: String,
        preferenceManager: PreferenceManager,
        appResourceMx: AppResourceMx
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
                    query,
                    preferenceManager,
                    appResourceMx
                )
            }
        ).liveData

    fun fetchWoList(): List<WoCacheEntity> {
        return woCacheDao.findAllWo()
    }

    fun fetchLocalMarker(): List<LocationEntity> {
        return locationDao.locationList()
    }

    fun findWobyWonum(wonum: String): WoCacheEntity? {
        val woCacheEntity = woCacheDao.findWoByWonum(wonum)
        woCacheEntity.whatIfNotNull { return woCacheEntity }
        return null
    }

    fun updateWo(woCacheEntity: WoCacheEntity, username: String?) {
        return woCacheDao.updateWo(woCacheEntity, username!!)
    }

    fun addWoToObjectBox(list: List<Member>) {
        for (wo in list) {
            var assignment: Assignment
            wo.assignment.whatIfNotNullOrEmpty(
                whatIf = {
                    assignment = it.get(0)
                    val laborCode: String = assignment.laborcode!!
                    val woCacheEntity = WoCacheEntity(
                        syncBody = WoUtils.convertMemberToBody(wo),
                        woId = wo.workorderid,
                        wonum = wo.wonum,
                        status = wo.status,
                        isChanged = BaseParam.APP_TRUE,
                        isLatest = BaseParam.APP_TRUE,
                        syncStatus = BaseParam.APP_TRUE,
                        laborCode = laborCode
                    )
                    setupWoLocation(woCacheEntity, wo)
                    woCacheEntity.createdDate = Date()
                    woCacheEntity.createdBy = appSession.userEntity.username
                    woCacheEntity.updatedBy = appSession.userEntity.username
                    saveWoList(woCacheEntity, appSession.userEntity.username)
                })
        }
    }


    private fun setupWoLocation(woCacheEntity: WoCacheEntity, wo: Member) {
        woCacheEntity.latitude = if (!wo.woserviceaddress.isNullOrEmpty()) {
            wo.woserviceaddress!![0].latitudey
        } else {
            null
        }
        woCacheEntity.longitude = if (!wo.woserviceaddress.isNullOrEmpty()) {
            wo.woserviceaddress!![0].longitudex
        } else {
            null
        }
    }

    suspend fun locationMarker (
        cookie: String,
        savedQuery: String,
        select: String,
        onSuccess: (FsmLocation) -> Unit,
        onError: (String) -> Unit
    ){   val response = workOrderClient.LocationMarker(cookie, savedQuery,select)
         response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //TODO
                //Save user session into local cache
                onSuccess(response)
                Timber.tag(TAG).i("repository locationMarker() code:%s", response.member)
                Timber.tag(TAG).i("repository locationMarker() code:%s", statusCode.code)
            }
        }.onError {
            Timber.tag(TAG).i("createWo() code: %s error: %s", statusCode.code, message())
            onError(message())
        }

    }


}