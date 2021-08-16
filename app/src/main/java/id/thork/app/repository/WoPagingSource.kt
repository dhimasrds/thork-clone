package id.thork.app.repository

import androidx.paging.PagingSource
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
    private val wappr : String?
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
        Timber.d("error loadresult %s", error)
        return try {
            if (query == null) {
                fetchWo(position)
                checkWoOnLocal()
                if (error && checkWoOnLocal().isEmpty()) {
                    return LoadResult.Error(Exception())
                }else if(error && checkWoOnLocal().isNotEmpty() && position > 1){
                    return LoadResult.Error(Exception())
                }
            }
            val wo = if (wappr !=null) {
                getWoAppr(offset)
//                when {
//                    emptyList -> {
//                        Timber.d("emptylist paging source :%s", emptyList)
//                        searchWoFromServer()
//                        searchLoadWoCache(offset, query.toString())
//                    }
//                    else -> searchLoadWoCache(offset, query)
//                }
            } else {
                loadWoCache(offset)
            }
            Timber.d("filter paging source :%s", query)
            Timber.d("filter paging wappr :%s", wappr)
            Timber.d("filter paging source wo size:%s", wo)
            loadResultPage(wo!!, position)
        } catch (exception: IOException) {
            Timber.d("exception :%s", exception)
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }



    private suspend fun fetchWo(position: Int): Boolean {
        val select: String = ApiParam.API_SELECT_ALL
        val savedQuery = appResourceMx.fsmResWorkorder
        Timber.tag(TAG).d("fetchWo() position: %s savedQuery: %s", position, savedQuery)
        savedQuery?.let {
            repository.getWorkOrderList(
                it, select, pageno = position, pagesize = 10,
                onSuccess = {
                    response = it
                    Timber.tag(TAG).d("fetchWo() response: %s", response)
                    it.member.whatIfNotNullOrEmpty { listmember ->
                        checkingWoInObjectBox(listmember)
                    }
                    error = false
                },
                onError = {
                    error = true
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
        val select: String = ApiParam.API_SELECT_ALL
        val wonum: String = query.toString()
        val where: String =
            ApiParam.WORKORDER_WHERE_LABORCODE_NEW + "\"" + laborcode + "\"" + ApiParam.WORKORDER_WHERE_STATUS_SEARCH + wonum + "%\"" + "}"

        appSession.userHash.whatIfNotNullOrEmpty { userHash ->
            repository.searchWorkOrder(
                userHash, select, where,
                onSuccess = {
                    response = it
                    Timber.d("emptylist paging source :%s", it.member)
                    it.member.whatIfNotNullOrEmpty { listmember ->
                        checkingWoInObjectBox(listmember)
                    }
                },
                onError = {
                },
                onException = {
                })
        }
        return error
    }

    private fun loadResultPage(list: List<Member>, position: Int): LoadResult<Int, Member> {

            return try {
                LoadResult.Page(
                    data = list,
                    prevKey = null,
                    nextKey = if (list.isEmpty()) {
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

    private fun checkingWoInObjectBox(list: List<Member>) {
        if (woCacheDao.findAllWo().isEmpty()) {
            repository.addWoToObjectBox(list)
        } else {
            repository.addObjectBoxToHashMap()
            repository.compareWoLocalWithServer(list)
        }
    }

    private fun findWo(offset: Int): String {
        //TODO change parameter to latest wo
        val cacheEntities: List<WoCacheEntity> = woCacheDao.findWoByisLatest(BaseParam.APP_TRUE, offset)
        val body = ArrayList<String>()
        for (i in cacheEntities.indices) {
            if (cacheEntities[i].status != null
                && !cacheEntities[i].status.equals(BaseParam.WAPPR)
                && !cacheEntities[i].status.equals(BaseParam.COMPLETED)
            ) {
                cacheEntities[i].syncBody.whatIfNotNull {
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

    private fun queryWoWappr(offset: Int): String {
        val list: List<WoCacheEntity> = woCacheDao.findListWoByStatusOffsetAndRfid(offset, BaseParam.WAPPR)
        Timber.d("queryWoWappr list :%s", list.size)
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

    private fun getWoAppr(offset: Int): List<Member>? {
        val moshi = Moshi.Builder().build()
        val listMyData: Type = Types.newParameterizedType(
            MutableList::class.java,
            Member::class.java
        )
        val adapter = moshi.adapter<List<Member>>(listMyData)
        val memberList: List<Member>? = adapter.fromJson(queryWoWappr(offset))
        Timber.d("memberlist search: %s", memberList?.size)
        Timber.d("memberlist search: %s", memberList)
        if (memberList.isNullOrEmpty()) {
            emptyList = true
        }
        return memberList
    }
}