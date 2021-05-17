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
import com.squareup.moshi.Moshi
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.response.asset_response.AssetResponse
import id.thork.app.network.response.fsm_location.FsmLocation
import id.thork.app.network.response.work_order.Assignment
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.*
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.persistence.entity.MultiAssetEntity
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.utils.StringUtils
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
    private val multiAssetDao: MultiAssetDao

    init {
        locationDao = LocationDaoImp()
        multiAssetDao = MultiAssetDaoImp()
    }

    var woListObjectBox: HashMap<String, WoCacheEntity>? = null

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
        cookie: String, xMethodeOverride: String, contentType: String,
        workOrderId: Int, body: Member,
        onSuccess: (WorkOrderResponse) -> Unit, onError: (String) -> Unit,
    ) {
        val response =
            workOrderClient.updateStatus(
                cookie,
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

    fun saveMultiAssetToObjectBox(multiAssetEntity: MultiAssetEntity) {
        multiAssetDao.save(multiAssetEntity, appSession.userEntity.username.toString())
    }

    fun deleteMultiAsset() {
        multiAssetDao.remove()
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
                    multiAssetToObjectBox(wo)
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
                multiAssetToObjectBox(member)
                woCacheEntity.createdDate = Date()
                woCacheEntity.createdBy = appSession.userEntity.username
                woCacheEntity.updatedBy = appSession.userEntity.username
                saveWoList(woCacheEntity, appSession.userEntity.username)
            })
    }

    fun replaceWolocalChace(
        woCacheEntity: WoCacheEntity,
        member: id.thork.app.network.response.work_order.Member,
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

    fun deleteWoEntity() {
        return woCacheDao.remove()
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

    fun updateWoCacheBeforeSync(
        woId: Int?,
        wonum: String?,
        status: String?,
        longdesc: String?,
        nextStatus: String,
    ) {
        val currentWoCache: WoCacheEntity? =
            wonum?.let {
                status?.let { woStatus ->
                    woCacheDao.findWoByWonumAndStatus(
                        it,
                        woStatus
                    )
                }
            }

        val moshi = Moshi.Builder().build()
        val memberJsonAdapter = moshi.adapter(Member::class.java)
        val currentMember: Member? = memberJsonAdapter.fromJson(currentWoCache?.syncBody)
        currentMember?.status = nextStatus
        currentMember?.descriptionLongdescription = longdesc

        if (currentWoCache?.isLatest == BaseParam.APP_TRUE) {
            currentWoCache.syncStatus = BaseParam.APP_FALSE
            currentWoCache.isLatest = BaseParam.APP_FALSE
//            currentWoCache.let { updateWo(it, appSession.userEntity.username) }
            updateWo(currentWoCache, appSession.userEntity.username)

            val newWoCache = WoCacheEntity()
            newWoCache.createdDate = Date()
            newWoCache.updatedDate = Date()
            newWoCache.createdBy = appSession.userEntity.username
            newWoCache.updatedBy = appSession.userEntity.username
            newWoCache.syncBody = memberJsonAdapter.toJson(currentMember)
            newWoCache.syncStatus = BaseParam.APP_FALSE
            newWoCache.isChanged = BaseParam.APP_TRUE
            newWoCache.isLatest = BaseParam.APP_TRUE
            newWoCache.laborCode = appSession.laborCode
            newWoCache.wonum = wonum
            newWoCache.status = nextStatus
            newWoCache.woId = woId
            saveWoList(newWoCache, appSession.userEntity.username)
        }
    }

    fun updateWoCacheAfterSync(
        wonum: String?,
        longdesc: String?,
        nextStatus: String,
    ) {
        val currentWoCache: WoCacheEntity? = wonum?.let {
            nextStatus.let { it1 ->
                woCacheDao.findWoByWonumAndStatus(
                    it,
                    it1
                )
            }
        }

        val moshi = Moshi.Builder().build()
        val memberJsonAdapter = moshi.adapter(Member::class.java)
        val currentMember: Member? = memberJsonAdapter.fromJson(currentWoCache?.syncBody)
        currentMember?.status = nextStatus
        currentMember?.descriptionLongdescription = longdesc

        currentWoCache?.syncBody = memberJsonAdapter.toJson(currentMember)
        currentWoCache?.syncStatus = BaseParam.APP_TRUE
        currentWoCache?.isChanged = BaseParam.APP_FALSE
        currentWoCache?.isLatest = BaseParam.APP_TRUE
        updateWo(currentWoCache!!, appSession.userEntity.username)
//        if (currentWoCache != null) {
//            updateWo(currentWoCache, appSession.userEntity.username)
//        }
    }

    fun addObjectBoxToHashMapActivity() {
        Timber.d("queryObjectBoxToHashMap()")
        if (woCacheDao.findAllWo().isNotEmpty()) {
            woListObjectBox = HashMap<String, WoCacheEntity>()
            val cacheEntities: List<WoCacheEntity> = woCacheDao.findAllWo()
            for (i in cacheEntities.indices) {
                if (cacheEntities[i].status != null
                    && cacheEntities[i].status.equals(BaseParam.COMPLETED)
                ) {
                    woListObjectBox!![cacheEntities[i].wonum!!] = cacheEntities[i]
                    Timber.d("HashMap value: %s", woListObjectBox!![cacheEntities[i].wonum])
                }
            }
        }
    }

    fun compareWoLocalActivityWithServer(list: List<Member>) {
        for (wo in list) {
            if (woListObjectBox!![wo.wonum!!] != null) {
                val woCahce = findWobyWonum(wo.wonum.toString())
                val dateMaximo = StringUtils.convertTimeString(wo.changedate.toString())
                val dateWoCache = StringUtils.convertTimeString(woCahce?.changeDate.toString())
                Timber.tag(TAG).d("compareWoLocalWithServer() date Maximo convert: ${dateMaximo}")
                if (woCahce?.syncStatus?.equals(BaseParam.APP_TRUE) == true && dateMaximo > dateWoCache) {
                    Timber.tag(TAG).d("compareWoLocalWithServer() replace wo local cache")
                    replaceWolocalChace(woCahce, wo)
                }
            } else if (woListObjectBox!![wo.status] != null && woListObjectBox!![wo.status]!!.equals(
                    BaseParam.COMPLETED)
            ) {
                Timber.tag(TAG).d("compareWoLocalWithServer() add new Wo")
                addWoToObjectBox(wo)
            }
        }
    }

    fun addObjectBoxToHashMap() {
        Timber.d("queryObjectBoxToHashMap()")
        if (woCacheDao.findAllWo().isNotEmpty()) {
            woListObjectBox = HashMap<String, WoCacheEntity>()
            val cacheEntities: List<WoCacheEntity> = woCacheDao.findAllWo()
            cacheEntities.whatIfNotNullOrEmpty { caches ->
                for (i in caches.indices) {
                    caches[i].wonum.whatIfNotNullOrEmpty { cachesWo ->
                        woListObjectBox!![cachesWo] = caches[i]
                        Timber.d("HashMap value: %s", woListObjectBox!![cachesWo])
                    }
                }
            }
        }
    }

    fun compareWoLocalWithServer(list: List<Member>) {
        for (wo in list) {
            if (woListObjectBox!![wo.wonum!!] != null) {
                val woCahce = findWobyWonum(wo.wonum.toString())
                val dateMaximo = StringUtils.convertTimeString(wo.changedate.toString())
                val dateWoCache = StringUtils.convertTimeString(woCahce?.changeDate.toString())
                Timber.tag(TAG).d("compareWoLocalWithServer() date Maximo convert: ${dateMaximo}")
                if (woCahce?.syncStatus?.equals(BaseParam.APP_TRUE) == true && dateMaximo > dateWoCache) {
                    Timber.tag(TAG).d("compareWoLocalWithServer() replace wo local cache")
                    replaceWolocalChace(woCahce, wo)
                }
            } else {
                Timber.tag(TAG).d("compareWoLocalWithServer() add new Wo")
                addWoToObjectBox(wo)
            }
        }
    }

    fun multiAssetToObjectBox(member: Member){
        member.multiassetlocci.whatIfNotNullOrEmpty {
            for (multiAsset in it) {
                val multiAssetEntity = MultiAssetEntity()
                multiAssetEntity.multiassetId = multiAsset.multiid
                multiAssetEntity.progress = multiAsset.progress == true
                multiAssetEntity.asssetId = multiAsset.asset?.get(0)?.assetid
                multiAssetEntity.description = multiAsset.asset?.get(0)?.description

                multiAsset.asset?.get(0)?.imagelibref.whatIfNotNull {
                    multiAssetEntity.image = it
                }

                multiAsset.asset?.get(0)?.thisfsmrfid.whatIfNotNull {
                    multiAssetEntity.thisfsmrfid = it
                }

                multiAsset.asset?.get(0)?.thisfsmtagtime.whatIfNotNull {
                    multiAssetEntity.thisfsmtagtime = it
                }

                multiAssetEntity.assetNum = multiAsset.assetnum
                multiAssetEntity.location = multiAsset.location
                multiAssetEntity.wonum = member.wonum
                multiAssetEntity.workorderId = member.workorderid
                multiAssetEntity.siteid = member.siteid
                multiAssetEntity.orgid = member.orgid
                saveMultiAssetToObjectBox(multiAssetEntity)
            }
        }
    }

}