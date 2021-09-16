package id.thork.app.repository

import android.content.Context
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
import id.thork.app.helper.CookieHelper
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.response.asset_response.AssetResponse
import id.thork.app.network.response.fsm_location.FsmLocation
import id.thork.app.network.response.material_response.MaterialResponse
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.network.response.worklogtype_response.WorklogtypeResponse
import id.thork.app.persistence.dao.*
import id.thork.app.persistence.entity.*
import id.thork.app.utils.DateUtils
import id.thork.app.utils.StringUtils
import id.thork.app.utils.WoUtils
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderRepository @Inject constructor(
    private val context: Context,
    private val workOrderClient: WorkOrderClient,
    private val woCacheDao: WoCacheDao,
    private val appSession: AppSession,
    private val assetDao: AssetDao,
    private val attachmentRepository: AttachmentRepository,
    private val materialRepository: MaterialRepository,
    private val storeroomRepository: StoreroomRepository,
    private val worklogRepository: WorklogRepository,
    private val taskRepository: TaskRepository,
    private val laborRepository: LaborRepository
) : BaseRepository() {
    val TAG = WorkOrderRepository::class.java.name
    private val locationDao: LocationDao
    private val multiAssetDao: MultiAssetDao

    init {
        locationDao = LocationDaoImp()
        multiAssetDao = MultiAssetDaoImp()
    }

    var woListObjectBox: HashMap<String, WoCacheEntity>? = null

    suspend fun getWorkOrderList(
        savedQuery: String,
        select: String,
        pageno: Int,
        pagesize: Int,
        onSuccess: (WorkOrderResponse) -> Unit,
        onError: (String) -> Unit,
        onException: (String) -> Unit,
    ) {
        var maxAuth = BaseParam.APP_EMPTY_STRING
        appSession.userEntity.userHash.whatIfNotNull {
            maxAuth = it
        }
        val cookie = CookieHelper(context, maxAuth).generateCookieIfExpired()
        Timber.tag(TAG).i("getWorkOrderList() cookie:%s", cookie)

        val response = workOrderClient.getWorkOrderList(
            cookie, savedQuery, select, pageno, pagesize
        )
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                //TODO
                //Save user session into local cache
                onSuccess(response)
                Timber.tag(TAG).i("getWorkOrderList() response:%s", response.member)
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
        properties: String,
        body: Member,
        onSuccess: (Member) -> Unit,
        onError: (String) -> Unit,
    ) {
        val response = workOrderClient.createWo(headerParam, properties, body)
        response.suspendOnSuccess {
            data.whatIfNotNull {
                onSuccess(it)
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
        cookie: String, xMethodeOverride: String, contentType: String, patchType: String,
        workOrderId: Int, body: Member,
        onSuccess: () -> Unit, onError: (String) -> Unit,
    ) {
        val response =
            workOrderClient.updateStatus(
                cookie,
                xMethodeOverride,
                contentType,
                patchType,
                workOrderId,
                body
            )
        response.suspendOnSuccess {
            onSuccess()
            Timber.tag(TAG).i("updateStatus() code: %s ", statusCode.code)
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
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WoPagingSource(
                    appSession = appSession,
                    repository = workOrderRepository,
                    woCacheDao,
                    null,
                    preferenceManager,
                    appResourceMx,
                    null
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
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WoPagingSource(
                    appSession = appSession,
                    repository = workOrderRepository,
                    woCacheDao,
                    query,
                    preferenceManager,
                    appResourceMx,
                    null
                )
            }
        ).liveData

    fun getWoWappr(
        appSession: AppSession,
        workOrderRepository: WorkOrderRepository,
        preferenceManager: PreferenceManager,
        appResourceMx: AppResourceMx,
        wappr: String
    ) =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                WoPagingSource(
                    appSession = appSession,
                    repository = workOrderRepository,
                    woCacheDao,
                    null,
                    preferenceManager,
                    appResourceMx,
                    wappr
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
        val woCacheEntity = woCacheDao.findWoByWonumAndIslatest(wonum, BaseParam.APP_TRUE)
        woCacheEntity.whatIfNotNull { return woCacheEntity }
        return null
    }

    fun fetchWoListOffline(syncStatus: Int, isChange: Int): List<WoCacheEntity> {
        return woCacheDao.findWoListBySyncStatusAndisChange(syncStatus, isChange)
    }


    fun updateWo(woCacheEntity: WoCacheEntity, username: String?) {
        return woCacheDao.updateWo(woCacheEntity, username!!)
    }

    fun addWoToObjectBox(list: List<Member>) {
        for (wo in list) {
            Timber.d("addWoToObjectBox() wonum: %s", wo.wonum)
            val laborCode: String = appSession.laborCode.toString()
            val woCacheEntity = WoCacheEntity(
                syncBody = WoUtils.convertMemberToBody(wo),
                woId = wo.workorderid,
                wonum = wo.wonum,
                status = wo.status,
                isChanged = BaseParam.APP_FALSE,
                isLatest = BaseParam.APP_TRUE,
                syncStatus = BaseParam.APP_TRUE,
                laborCode = laborCode,
                reportDateUTCTime = DateUtils.convertStringToMaximoDate(wo.reportdate),
                reportString = DateUtils.convertMxDateStringToString(
                    DateUtils.convertStringToMaximoDate(wo.reportdate)
                )
            )
            woCacheEntity.changeDate = wo.changedate
            setupWoLocation(woCacheEntity, wo)
            multiAssetToObjectBox(wo)
            woCacheEntity.createdDate = Date()
            woCacheEntity.createdBy = appSession.userEntity.username
            woCacheEntity.updatedBy = appSession.userEntity.username
            laborRepository.addLaborPlanToObjectBox(wo)
            laborRepository.addLaborActualToObjectBox(wo)
            saveWoList(woCacheEntity, appSession.userEntity.username)

            runBlocking {
                Timber.tag(TAG).d("addWoToObjectBox() doclinks")
                wo.doclinks.whatIfNotNull { doclinks ->
                    Timber.tag(TAG).d("addWoToObjectBox() doclinks: %s", doclinks)
                    appSession.userEntity.username.whatIfNotNullOrEmpty { username ->
                        doclinks.href.whatIfNotNullOrEmpty { href ->
                            Timber.tag(TAG).d(
                                "addWoToObjectBox() username: %s href: %s",
                                username, href
                            )
                            attachmentRepository.createAttachmentFromDocklinks(
                                href,
                                username
                            )
                        }
                    }
                }
            }

            wo.wpmaterial.whatIfNotNullOrEmpty {
                wo.workorderid?.let { woId ->
                    materialRepository.addListMaterialPlanToObjectBox(
                        it,
                        wo.wonum.toString(),
                        woId
                    )
                }
            }

            wo.worklog.whatIfNotNullOrEmpty {
                wo.wonum.whatIfNotNull { wonum ->
                    wo.workorderid.whatIfNotNull { woid ->
                        worklogRepository.saveWorklogToObjectBox(it, woid, wonum)
                    }
                }
            }

            wo.woactivity.whatIfNotNullOrEmpty {
                wo.workorderid.whatIfNotNull { woid ->
                    wo.wonum.whatIfNotNull { wonum ->
                        taskRepository.handlingTaskSuccesFromWoDetail(it, woid, wonum)
                    }
                }
            }
        }
    }

    fun addWoToObjectBox(member: Member) {
        val laborCode: String = appSession.laborCode.toString()
        val woCacheEntity = WoCacheEntity(
            syncBody = WoUtils.convertMemberToBody(member),
            woId = member.workorderid,
            wonum = member.wonum,
            status = member.status,
            isChanged = BaseParam.APP_FALSE,
            isLatest = BaseParam.APP_TRUE,
            syncStatus = BaseParam.APP_TRUE,
            laborCode = laborCode,
            reportDateUTCTime = DateUtils.convertStringToMaximoDate(member.reportdate),
            reportString = DateUtils.convertMxDateStringToString(
                DateUtils.convertStringToMaximoDate(member.reportdate)
            )
        )
        setupWoLocation(woCacheEntity, member)
        woCacheEntity.changeDate = member.changedate
        multiAssetToObjectBox(member)
        woCacheEntity.createdDate = Date()
        woCacheEntity.createdBy = appSession.userEntity.username
        woCacheEntity.updatedBy = appSession.userEntity.username
        laborRepository.addLaborPlanToObjectBox(member)
        laborRepository.addLaborActualToObjectBox(member)
        saveWoList(woCacheEntity, appSession.userEntity.username)

        runBlocking {
            Timber.tag(TAG).d("addWoToObjectBox() doclinks")
            member.doclinks.whatIfNotNull { doclinks ->
                Timber.tag(TAG).d("addWoToObjectBox() doclinks: %s", doclinks)
                appSession.userEntity.username.whatIfNotNullOrEmpty { username ->
                    doclinks.href.whatIfNotNullOrEmpty { href ->
                        Timber.tag(TAG).d(
                            "addWoToObjectBox() username: %s href: %s",
                            username, href
                        )
                        attachmentRepository.createAttachmentFromDocklinks(
                            href,
                            username
                        )
                    }
                }
            }
        }

        member.wpmaterial.whatIfNotNullOrEmpty {
            member.workorderid?.let { woId ->
                materialRepository.addListMaterialPlanToObjectBox(
                    it,
                    member.wonum.toString(),
                    woId
                )
            }
        }

        member.woactivity.whatIfNotNullOrEmpty {
            member.workorderid.whatIfNotNull { woid ->
                member.wonum.whatIfNotNull { wonum ->
                    taskRepository.handlingTaskSuccesFromWoDetail(it, woid, wonum)
                }
            }
        }

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
        woCacheEntity.isChanged = BaseParam.APP_FALSE
        woCacheEntity.isLatest = BaseParam.APP_TRUE
        woCacheEntity.syncStatus = BaseParam.APP_FALSE
        woCacheEntity.updatedBy = appSession.userEntity.username
        woCacheEntity.reportDateUTCTime = DateUtils.convertStringToMaximoDate(member.reportdate)
        woCacheEntity.reportString = DateUtils.convertMxDateStringToString(
            DateUtils.convertStringToMaximoDate(member.reportdate)
        )
        saveWoList(woCacheEntity, appSession.userEntity.username)
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
            Timber.tag(TAG).i("locationMarker() code: %s error: %s", statusCode.code, message())
            onError(message())
        }
    }

    suspend fun getItemMaster(
        cookie: String,
        select: String,
        onSuccess: (MaterialResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = workOrderClient.getItemMaster(cookie, select)
        response.suspendOnSuccess {
            data.whatIfNotNull {
                onSuccess(it)
                Timber.tag(TAG).i("repository getItemMaster() code:%s", it.member)
                Timber.tag(TAG).i("repository getItemMaster() code:%s", statusCode.code)
            }
        }.onError {
            Timber.tag(TAG).i("getItemMaster() code: %s error: %s", statusCode.code, message())
            onError(message())
        }
    }

    suspend fun getWorklogType(
        cookie: String,
        select: String,
        where: String,
        onSuccess: (WorklogtypeResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = workOrderClient.getWorklogType(cookie, select, where)
        response.suspendOnSuccess {
            data.whatIfNotNull {
                onSuccess(it)
            }
        }.onError {
            Timber.tag(TAG).i("getWorklogType() code: %s error: %s", statusCode.code, message())
            onError(message())
        }
    }

    suspend fun getStoreroom(
        cookie: String,
        select: String,
        onSuccess: (MaterialResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        val response = workOrderClient.getItemMaster(cookie, select)
        response.suspendOnSuccess {
            data.whatIfNotNull {
                onSuccess(it)
                Timber.tag(TAG).i("repository getItemMaster() code:%s", it.member)
                Timber.tag(TAG).i("repository getItemMaster() code:%s", statusCode.code)
            }
        }.onError {
            Timber.tag(TAG).i("getItemMaster() code: %s error: %s", statusCode.code, message())
            onError(message())
        }
    }

    fun addLocationToObjectBox(member: List<id.thork.app.network.response.fsm_location.Member>) {
        deleteLocation()
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
        deleteAssetEntity()
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
                assetrfid = asset.thisfsmrfid,
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

        Timber.tag(TAG).d("updateWo() updateWoCacheBeforeSync() %s", currentWoCache?.status)
        val listMatAct = materialRepository.prepareMaterialActual(woId, wonum)
        val moshi = Moshi.Builder().build()
        val memberJsonAdapter = moshi.adapter(Member::class.java)
        val currentMember: Member? = memberJsonAdapter.fromJson(currentWoCache?.syncBody)
        currentMember?.status = nextStatus
        currentMember?.descriptionLongdescription = longdesc
        listMatAct.whatIfNotNullOrEmpty {
            currentMember?.matusetrans = it
        }

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
            newWoCache.longitude = currentWoCache.longitude
            newWoCache.latitude = currentWoCache.latitude
            newWoCache.changeDate = DateUtils.getDateTimeMaximo()
            saveWoList(newWoCache, appSession.userEntity.username)
        }
    }

    fun updateWoCacheAfterSync(
        woId: Int?,
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

        Timber.tag(TAG).d("updateWo() updateWoCacheAfterSync() %s", currentWoCache?.status)
        val listMatAct = materialRepository.prepareMaterialActual(woId, wonum)
        val moshi = Moshi.Builder().build()
        val memberJsonAdapter = moshi.adapter(Member::class.java)
        val currentMember: Member? = memberJsonAdapter.fromJson(currentWoCache?.syncBody)
        currentMember?.status = nextStatus
        longdesc.whatIfNotNullOrEmpty {
            currentMember?.descriptionLongdescription = longdesc
        }
        listMatAct.whatIfNotNullOrEmpty {
            currentMember?.matusetrans = it
        }

        currentWoCache?.syncBody = memberJsonAdapter.toJson(currentMember)
        currentWoCache?.syncStatus = BaseParam.APP_TRUE
        currentWoCache?.isChanged = BaseParam.APP_FALSE
        currentWoCache?.isLatest = BaseParam.APP_TRUE
        updateWo(currentWoCache!!, appSession.userEntity.username)
    }

    fun updateCreateWoCacheOfflineMode(
        longdesc: String?,
        currentWo: WoCacheEntity,
        member: Member
    ) {
        Timber.d("updateCreateWoCacheOfflineMode() syncBody %s ", currentWo.syncBody)
        val woId = member.workorderid
        val wonum = member.wonum
        val listMatAct = materialRepository.prepareMaterialActual(woId, wonum)
        val moshi = Moshi.Builder().build()
        val memberJsonAdapter = moshi.adapter(Member::class.java)
        val currentMember = memberJsonAdapter.fromJson(currentWo.syncBody)
        longdesc.whatIfNotNullOrEmpty {
            currentMember?.descriptionLongdescription = longdesc
        }
        listMatAct.whatIfNotNullOrEmpty {
            currentMember?.matusetrans = it
        }

        currentWo.woId = woId
        currentWo.wonum = wonum
        currentWo.laborCode = appSession.laborCode
        currentWo.syncBody = memberJsonAdapter.toJson(member)
        currentWo.syncStatus = BaseParam.APP_TRUE
        currentWo.isChanged = BaseParam.APP_FALSE
        currentWo.isLatest = BaseParam.APP_TRUE
        updateWo(currentWo, appSession.userEntity.username)

    }

    fun updateCreateWoCacheOnlineMode(
        woId: Int?,
        wonum: String?,
        tempWonum: String,
        member: Member
    ) {
        val syncBody = WoUtils.convertMemberToBody(member)
        val woCache = woCacheDao.findWoByWonum(tempWonum)
        woCache.whatIfNotNull {
            it.woId = woId
            it.wonum = wonum
            it.syncBody = syncBody
            it.laborCode = appSession.laborCode
            it.syncStatus = BaseParam.APP_TRUE
            it.isChanged = BaseParam.APP_FALSE
            it.isLatest = BaseParam.APP_TRUE
            updateWo(it, appSession.userEntity.username)
        }
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
                    BaseParam.COMPLETED
                )
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

    private fun multiAssetToObjectBox(member: Member) {
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
                multiAssetEntity.isScan = BaseParam.APP_FALSE
                saveMultiAssetToObjectBox(multiAssetEntity)
            }
        }
    }

    private fun saveDoclinkToAttachment() {
        val attachmentEntities: MutableList<AttachmentEntity> = mutableListOf()
        val attachmentEntity = AttachmentEntity()
        attachmentEntities.add(attachmentEntity)
    }

    fun findByAssetnum(assetnum: String): AssetEntity? {
        return assetDao.findByAssetnum(assetnum)
    }

    fun findByLocation(location: String): LocationEntity? {
        return locationDao.findByLocation(location)
    }

    fun saveCreatedWoLocally(
        member: Member,
        tempWonum: String,
        externalrefid: String
    ) {
        val tWoCacheEntity = WoCacheEntity()
        tWoCacheEntity.syncBody = WoUtils.convertMemberToBody(member)
        tWoCacheEntity.syncStatus = BaseParam.APP_FALSE
        tWoCacheEntity.isChanged = BaseParam.APP_TRUE
        tWoCacheEntity.createdBy = appSession.userEntity.username
        tWoCacheEntity.updatedBy = appSession.userEntity.username
        tWoCacheEntity.createdDate = Date()
        tWoCacheEntity.updatedDate = Date()
        tWoCacheEntity.wonum = tempWonum
        tWoCacheEntity.status = BaseParam.WAPPR
        tWoCacheEntity.isLatest = BaseParam.APP_TRUE
        tWoCacheEntity.externalREFID = externalrefid
        saveWoList(tWoCacheEntity, appSession.userEntity.username)
    }


    //Submodule Labor
    suspend fun createLaborPlan(
        cookie: String,
        xMethodeOverride: String,
        contentType: String,
        patchType: String,
        properties: String,
        workOrderId: Int,
        body: Member,
        onSuccess: (Member) -> Unit,
        onError: (String) -> Unit,
    ) {
        val response =
            workOrderClient.createLaborPlan(
                cookie,
                xMethodeOverride,
                contentType,
                patchType,
                properties,
                workOrderId,
                body
            )
        response.suspendOnSuccess {
            data.whatIfNotNull {
                onSuccess(it)
            }
            Timber.tag(TAG).i("createLaborPlan() code: %s ", statusCode.code)
        }
            .onError {
                Timber.tag(TAG)
                    .i("createLaborPlan() code: %s error: %s", statusCode.code, message())
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("createLaborPlan() exception: %s", message())
                onError(message())
            }
    }

    suspend fun updateLaborPlan(
        cookie: String, xMethodeOverride: String, contentType: String, patchType: String,
        workOrderId: Int, body: Member,
        onSuccess: () -> Unit, onError: (String) -> Unit,
    ) {
        val response =
            workOrderClient.updateLaborPlan(
                cookie,
                xMethodeOverride,
                contentType,
                patchType,
                workOrderId,
                body
            )
        response.suspendOnSuccess {
            onSuccess()
            Timber.tag(TAG).i("udpateLaborPlan() code: %s ", statusCode.code)
        }
            .onError {
                Timber.tag(TAG)
                    .i("udpateLaborPlan() code: %s error: %s", statusCode.code, message())
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("udpateLaborPlan() exception: %s", message())
                onError(message())
            }
    }

    suspend fun deleteLaborPlan(cookie: String, url : String,onSuccess: () -> Unit, onError: (String) -> Unit,) {
        val response = workOrderClient.deleteLaborPlan(cookie, url)

        response.suspendOnSuccess {
            onSuccess()
            Timber.tag(TAG).i("deleteLaborPlan() code: %s ", statusCode.code)
        }
            .onError {
                Timber.tag(TAG)
                    .i("deleteLaborPlan() code: %s error: %s", statusCode.code, message())
                onError(message())
            }

            .onException {
                Timber.tag(TAG).i("deleteLaborPlan() exception: %s", message())
                onError(message())
            }
    }

}