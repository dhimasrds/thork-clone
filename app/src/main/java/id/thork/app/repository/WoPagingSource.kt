package id.thork.app.repository

import androidx.paging.PagingSource
import com.google.gson.Gson
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppResourceMx
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.ApiParam
import id.thork.app.network.response.work_order.Assignment
import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.entity.WoCacheEntity
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Dhimas Saputra on 21/01/21
 * Jakarta, Indonesia.
 */


@Module
@InstallIn(ActivityRetainedComponent::class)
class WoPagingSource @Inject constructor(
    private val appSession: AppSession,
    private val repository: WorkOrderRepository,
    private val woCacheDao: WoCacheDao,
    private val query: String?,
    private val preferenceManager: PreferenceManager,
    private val appResourceMx: AppResourceMx,
) : PagingSource<Int, Member>() {

    val TAG = WoPagingSource::class.java.name
    var offset = 0
    var error = false
    var emptyList = false
    var woListObjectBox: HashMap<String, WoCacheEntity>? = null
    var response = WorkOrderResponse()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Member> {
        val position = params.key ?: 1
        Timber.d("position :%s", position)
        return try {
            if (query == null) {
                fetchWo(position)
                checkWoOnLocal()
                if (error && checkWoOnLocal().isEmpty()) {
                    return LoadResult.Error(Exception())
                } else if (error && checkWoOnLocal().isNotEmpty() && position > 1) {
                    return LoadResult.Error(Exception())
                }
            }

            val wo = if (query != null) {
                searchLoadWoCache(offset, query)

                when {
                    emptyList -> {
                        Timber.d("emptylist paging source :%s", emptyList)
                        searchWoFromServer()
                        searchLoadWoCache(offset, query.toString())
                    }
                    else -> searchLoadWoCache(offset, query)
                }
            } else {
                loadWoCache(offset)
            }
            Timber.d("filter paging source :%s", query)
            Timber.d("filter paging source wo size:%s", wo)
            loadResultPage(wo, position)

        } catch (exception: IOException) {
            Timber.d("exception :%s", exception)
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }


    private suspend fun fetchWo(position: Int): Boolean {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val select: String = ApiParam.WORKORDER_SELECT
        val savedQuery = appResourceMx.fsmResWorkorder

        savedQuery?.let {
            repository.getWorkOrderList(
                cookie, it, select, pageno = position, pagesize = 10,
                onSuccess = {
                    response = it
                    checkingWoInObjectBox(response.member)
                    error = false
                },
                onError = {
                    error = false
                },
                onException = {
                    error = true
                })
        }
        return error
    }

    private fun checkWoOnLocal(): List<WoCacheEntity> {
        return woCacheDao.findAllWo()
    }

    private suspend fun searchWoFromServer(): Boolean {
        val laborcode: String? = appSession.laborCode
        val select: String = ApiParam.WORKORDER_SELECT
        val wonum: String = query.toString()
        val where: String =
            ApiParam.WORKORDER_WHERE_LABORCODE_NEW + "\"" + laborcode + "\"" + ApiParam.WORKORDER_WHERE_STATUS_SEARCH + wonum + "%\"" + "}"

        appSession.userHash.whatIfNotNullOrEmpty { userHash ->
            repository.searchWorkOrder(
                userHash, select, where,
                onSuccess = {
                    response = it
                    Timber.d("emptylist paging source :%s", it.member)
                    checkingWoInObjectBox(response.member)
                },
                onError = {
                },
                onException = {
                })
        }
        return error
    }

    private fun loadResultPage(list: List<Member>?, position: Int): LoadResult<Int, Member> {
        list.whatIfNotNullOrEmpty {
            return try {
                LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = if (it.isEmpty()) {
                        null
                    } else {
                        offset += 10
                        Timber.d("offset :%s", offset)
                        position + 1
                    }
                )
            } catch (exception: Exception) {
                return LoadResult.Error(exception)
            }
        }
        return LoadResult.Error(Exception("Result is empty"))
    }

    private fun checkingWoInObjectBox(list: List<Member>?) {
        list.whatIfNotNullOrEmpty {
            if (woCacheDao.findAllWo().isEmpty()) {
                addWoToObjectBox(it)
            } else {
                addObjectBoxToHashMap()
                compareWoLocalWithServer(it)
            }
        }

    }

    private fun addWoToObjectBox(list: List<Member>) {
        for (wo in list) {
            createNewWo(wo)
        }
    }

    private fun setupWoLocation(woCacheEntity: WoCacheEntity, wo: Member) {
        wo.woserviceaddress.whatIfNotNullOrEmpty { woserviceaddress ->
            woCacheEntity.latitude = if (!wo.woserviceaddress.isNullOrEmpty()) {
                woserviceaddress[0].latitudey
            } else {
                null
            }
        }

        wo.woserviceaddress.whatIfNotNullOrEmpty { woserviceaddress ->
            woCacheEntity.longitude = if (!wo.woserviceaddress.isNullOrEmpty()) {
                woserviceaddress[0].longitudex
            } else {
                null
            }
        }
    }

    private fun createNewWo(wo: Member) {
        var assignment: Assignment
        wo.assignment.whatIfNotNullOrEmpty(
            whatIf = {
                assignment = it.get(0)
                var laborCode: String? = null
                assignment.laborcode.whatIfNotNullOrEmpty {
                    laborCode = it
                }
                val woCacheEntity = WoCacheEntity(
                    syncBody = convertToJson(wo),
                    woId = wo.workorderid,
                    wonum = wo.wonum,
                    status = wo.status,
                    isChanged = BaseParam.APP_TRUE,
                    isLatest = BaseParam.APP_TRUE,
                    syncStatus = BaseParam.APP_FALSE,
                    laborCode = laborCode,
                    changeDate = wo.changedate

                )
                setupWoLocation(woCacheEntity, wo)
                woCacheEntity.createdDate = Date()
                woCacheEntity.createdBy = appSession.userEntity.username
                woCacheEntity.updatedBy = appSession.userEntity.username
                repository.saveWoList(woCacheEntity, appSession.userEntity.username)
            })
    }

    private fun addObjectBoxToHashMap() {
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

    private fun compareWoLocalWithServer(list: List<Member>) {
        for (wo in list) {
            Timber.d("compareWoLocalWithServer : %s", wo.wonum)
            woListObjectBox.whatIfNotNullOrEmpty {
                if (it[wo.wonum] != null) {

                } else {
                    createNewWo(wo)
                }
            }

        }
    }

    private fun convertToJson(member: Member): String? {
        val gson = Gson()
        return gson.toJson(member)
    }

    private fun findWo(offset: Int): String {
        val cacheEntities: List<WoCacheEntity> = woCacheDao.findAllWo(offset)
        val body = ArrayList<String>()
        for (i in cacheEntities.indices) {
            if (cacheEntities[i].status != null
                && !cacheEntities[i].status.equals(BaseParam.WAPPR)
                && !cacheEntities[i].status.equals(BaseParam.COMPLETED)
            ) {
                cacheEntities[i].syncBody.whatIfNotNullOrEmpty {
                    body.add(it)
                }
            }
        }
        Timber.d("json : %s", body.toString())
        return body.toString()
    }

    private fun searchFindWo(offset: Int, query: String): String {
        val list: List<WoCacheEntity> = woCacheDao.findWoByWonum(offset, query, BaseParam.COMPLETED)
        Timber.d("searchFindWo list :%s", list.size)
        val body = ArrayList<String>()
        for (i in list.indices) {
            if (list[i].status != null) {
                list[i].syncBody.whatIfNotNull {
                    body.add(it)
                }
            }
        }
        Timber.d(" searchFindWo json : %s", body.toString())
        return body.toString()
    }

    private fun searchLoadWoCache(offset: Int, query: String): List<Member>? {
        val moshi = Moshi.Builder().build()
        val listMyData: Type = Types.newParameterizedType(
            MutableList::class.java,
            Member::class.java
        )
        val adapter = moshi.adapter<List<Member>>(listMyData)
        val memberList: List<Member>? = adapter.fromJson(searchFindWo(offset, query))
        Timber.d("memberlist search: %s", memberList?.size)
        Timber.d("memberlist search: %s", memberList)
        if (memberList.isNullOrEmpty()) {
            emptyList = true
        }
        return memberList
    }

    fun loadWoCache(offset: Int): List<Member>? {
        val moshi = Moshi.Builder().build()
        val listMyData: Type = Types.newParameterizedType(
            MutableList::class.java,
            Member::class.java
        )
        val adapter = moshi.adapter<List<Member>>(listMyData)
        val memberList: List<Member>? = adapter.fromJson(findWo(offset))
        Timber.d("memberlist : %s", memberList?.size)
        return memberList
    }
}