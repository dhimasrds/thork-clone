package id.thork.app.repository

import androidx.paging.PagingSource
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppSession
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

/**
 * Created by Dhimas Saputra on 21/01/21
 * Jakarta, Indonesia.
 */


@Module
@InstallIn(ActivityRetainedComponent::class)
class WoPagingSource @Inject constructor(
    private val appSession: AppSession,
    private val repository: WorkOrderRepository,
    private val woCacheDao: WoCacheDao
) : PagingSource<Int, Member>() {

    val TAG = WoPagingSource::class.java.name
    var offset = 0
    var error = false

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Member> {
        val position = params.key ?: 1
        Timber.d("position :%s", position)
        return try {
            fetchWo(position)
            if (error) {
                return LoadResult.Error(Exception())
            }
            val wo = loadWoCache(offset)
            loadResultPage(wo!!, position)

        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }


    private suspend fun fetchWo(position: Int): Boolean {
        val laborcode: String? = appSession.laborCode
        val select: String = ApiParam.WORKORDER_SELECT
        val where: String =
            ApiParam.WORKORDER_WHERE_LABORCODE_NEW + "\"" + laborcode + "\"" + ApiParam.WORKORDER_WHERE_STATUS + "}"

        repository.getWorkOrderList(
            appSession.userHash!!, select, where, pageno = position, pagesize = 10,
            onSuccess = {
                saveWoToObjectBox(it.member)
                error = false
            },
            onError = {
                error = false
            },
            onException = {
                error = true
            })
        return error
    }

    private fun loadResultPage(list: List<Member>, position: Int): LoadResult<Int, Member> {
        return try {
            LoadResult.Page(
                data = list,
                prevKey = if (position == 1) null else position,
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

    private fun saveWoToObjectBox(list: List<Member>) {
        for (wo in list) {
            val woCacheEntity = WoCacheEntity(
                syncBody = convertToJson(wo),
                woId = wo.workorderid,
                wonum = wo.wonum,
                status = wo.status,
                isChanged = BaseParam.APP_TRUE,
                isLatest = BaseParam.APP_TRUE,
                syncStatus = BaseParam.APP_TRUE,
                laborCode = wo.cxlabor
            )
            woCacheEntity.createdDate = Date()
            woCacheEntity.createdBy = appSession.userEntity.username
            woCacheEntity.updatedBy = appSession.userEntity.username
            repository.saveWoList(woCacheEntity, appSession.userEntity.username)
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
            if (cacheEntities[i].status !=null
                && !cacheEntities[i].status.equals(BaseParam.COMPLETED)
                && !cacheEntities[i].status.equals(BaseParam.WAPPR)
            ){
            body.add(cacheEntities[i].syncBody!!)
            }
        }
        Timber.d("json : %s", body.toString())
        return body.toString()
    }

    @Throws(IOException::class)
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