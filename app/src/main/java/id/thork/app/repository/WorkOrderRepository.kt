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
import id.thork.app.network.response.asset_response.AssetResponse
import id.thork.app.network.response.work_order.Assignment
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.LocationDao
import id.thork.app.persistence.dao.LocationDaoImp
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.entity.AssetEntity
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
    private val assetDao: AssetDao,
) : BaseRepository {
    val TAG = WorkOrderRepository::class.java.name
    private val locationDao: LocationDao

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
            workOrderClient.updateStatus(
                headerParam,
                xMethodeOverride,
                contentType,
                workOrderId,
                body
            )
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

    fun saveLocationToObjectBox(locationEntity: LocationEntity): LocationEntity {
        return locationDao.saveLocation(locationEntity)
    }

    fun deleteLocation() {
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

    fun fetchAssetList(): List<AssetEntity> {
        return assetDao.findAllAsset()
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

    fun addWoToObjectBox(member: Member) {
        var assignment: Assignment
        member.assignment.whatIfNotNullOrEmpty(
            whatIf = {
                assignment = it.get(0)
                val laborCode: String = assignment.laborcode!!
                val woCacheEntity = WoCacheEntity(
                    syncBody = WoUtils.convertMemberToBody(member),
                    woId = member.workorderid,
                    wonum = member.wonum,
                    status = member.status,
                    isChanged = BaseParam.APP_TRUE,
                    isLatest = BaseParam.APP_TRUE,
                    syncStatus = BaseParam.APP_TRUE,
                    laborCode = laborCode
                )
                setupWoLocation(woCacheEntity, member)
                woCacheEntity.createdDate = Date()
                woCacheEntity.createdBy = appSession.userEntity.username
                woCacheEntity.updatedBy = appSession.userEntity.username
                saveWoList(woCacheEntity, appSession.userEntity.username)
            })
    }

    fun replaceWolocalChace(
        woCacheEntity: WoCacheEntity,
        member: id.thork.app.network.response.work_order.Member
    ) {
        woCacheEntity.syncBody = WoUtils.convertMemberToBody(member)
        woCacheEntity.woId = member.workorderid
        woCacheEntity.wonum = member.wonum
        woCacheEntity.status = member.status
        woCacheEntity.changeDate = member.changedate
        woCacheEntity.isChanged = BaseParam.APP_TRUE
        woCacheEntity.isLatest = BaseParam.APP_TRUE
        woCacheEntity.syncStatus = BaseParam.APP_FALSE
        woCacheEntity.updatedBy = appSession.userEntity.username
        saveWoList(woCacheEntity, appSession.userEntity.username)
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

    suspend fun locationMarker(
        cookie: String,
        savedQuery: String,
        select: String,
        onSuccess: (FsmLocation) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = workOrderClient.LocationMarker(cookie, savedQuery, select)
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

    fun addLocationToObjectBox(member: List<id.thork.app.network.response.fsm_location.Member>) {
        for (location in member) {
            val locationEntity = LocationEntity()
            locationEntity.location = location.location
            locationEntity.description = location.description
            locationEntity.status = location.status
            location.serviceaddress.whatIfNotNullOrEmpty {
                locationEntity.formatAddress = location.serviceaddress!![0].formattedaddress
                locationEntity.longitudex = location.serviceaddress!![0].longitudex
                locationEntity.latitudey = location.serviceaddress!![0].latitudey
            }
            locationEntity.thisfsmrfid = location.thisfsmrfid
            locationEntity.image = location.imagelibref
            locationEntity.thisfsmtagprogress = location.thisfsmtagprogress.toString()
            locationEntity.thisfsmtagtime = location.thisfsmtagtime
            saveLocationToObjectBox(locationEntity)
        }
    }

    suspend fun getAssetList(
        cookie: String,
        savedQuery: String,
        select: String,
        onSuccess: (AssetResponse) -> Unit,
        onError: (String) -> Unit,
        onException: (String) -> Unit
    ) {
        val response = workOrderClient.getAssetList(
            cookie, savedQuery, select
        )
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //TODO
                //Save user session into local cache
                onSuccess(response)
                Timber.tag(TAG).i("repository getAssetList() code:%s", statusCode.code)
            }
        }
            .onError {
                Timber.tag(TAG).i(
                    "repository getAssetList() : %s error: %s",
                    statusCode.code,
                    message()
                )
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("repository getAssetList() exception: %s", message())
                onException(message())
            }

    }

    fun saveAssetList(assetEntity: AssetEntity, username: String?): AssetEntity {
        return assetDao.createAssetCache(assetEntity, username)
    }

    fun deleteAssetEntity() {
        return assetDao.remove()
    }

    fun addAssetToObjectBox(list: List<id.thork.app.network.response.asset_response.Member>) {
        for (asset in list) {
            val assetEntity = AssetEntity(
                assetnum = asset.assetnum,
                description = asset.description,
                status = asset.status,
                assetLocation = asset.location,
                formattedaddress = asset.serviceaddress?.get(0)?.formattedaddress,
                siteid = asset.siteid,
                orgid = asset.orgid,
                latitudey = asset.serviceaddress?.get(0)?.latitudey,
                longitudex = asset.serviceaddress?.get(0)?.longitudex,
                assetRfid = asset.thisfsmrfid,
                image = asset.imagelibref,
                assetTagTime = asset.thisfsmtagtime
            )
            assetEntity.createdDate = Date()
            assetEntity.createdBy = appSession.userEntity.username
            assetEntity.updatedBy = appSession.userEntity.username
            saveAssetList(assetEntity, appSession.userEntity.username)
        }
    }

}