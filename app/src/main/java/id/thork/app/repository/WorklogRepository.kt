package id.thork.app.repository

import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.work_order.Worklog
import id.thork.app.network.response.worklogtype_response.Member
import id.thork.app.persistence.dao.WorklogDaoImp
import id.thork.app.persistence.dao.WorklogTypeDaoImp
import id.thork.app.persistence.entity.WorklogEntity
import id.thork.app.persistence.entity.WorklogTypeEntity
import id.thork.app.utils.DateUtils
import java.util.*
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 07/06/2021
 * Jakarta, Indonesia.
 */
class WorklogRepository @Inject constructor(
    private val worklogDaoImp: WorklogDaoImp,
    private val worklogTypeDaoImp: WorklogTypeDaoImp,
    private val appSession: AppSession
) {

    var username = appSession.userEntity.username

    /**
     * Worklog Type
     */
    fun saveWorklogtype(worklogtypelist: List<WorklogTypeEntity>): List<WorklogTypeEntity>? {
        return worklogTypeDaoImp.saveListWorklogType(worklogtypelist)
    }

    fun removeWorklogType() {
        return worklogTypeDaoImp.remove()
    }

    fun fetchListWorklogtype(): List<WorklogTypeEntity>? {
        return worklogTypeDaoImp.listWorklogType()
    }

    fun saveWorklogtypeToObjectBox(members: List<Member>) {
        removeWorklogType()
        members.whatIfNotNullOrEmpty {
            val listWorklogTypecache = mutableListOf<WorklogTypeEntity>()
            it.forEach {
                val cache = WorklogTypeEntity()
                cache.type = it.value
                cache.description = it.description
                cache.updatedBy = username
                cache.updatedDate = Date()
                cache.createdBy = username
                cache.createdDate = Date()
                listWorklogTypecache.add(cache)
            }
            saveWorklogtype(listWorklogTypecache)
        }
    }

    /**
     * Worklog
     */
    fun saveWorklog(worklogEntity: WorklogEntity) {
        return worklogDaoImp.save(worklogEntity, username.toString())
    }

    fun saveListWorkLog(worklogList: List<WorklogEntity>): List<WorklogEntity>? {
        return worklogDaoImp.saveListWorklog(worklogList)
    }

    fun removeWorklog() {
        return worklogDaoImp.remove()
    }

    fun fetchListWorklogByWoid(workorderid: String): List<WorklogEntity>? {
        return worklogDaoImp.findListWorklogByWoid(workorderid)
    }

    fun saveWorklogEntity(
        summary: String,
        description: String,
        type: String,
        wonum: String,
        workorderid: String
    ) {
        val worklogEntity = WorklogEntity()
        worklogEntity.summary = summary
        worklogEntity.description = description
        worklogEntity.type = type
        worklogEntity.date = DateUtils.getDateTimeMaximo()
        worklogEntity.syncStatus = BaseParam.APP_FALSE
        worklogEntity.wonum = wonum
        worklogEntity.workorderid = workorderid
        saveWorklog(worklogEntity)
    }

    fun getListWorklogByWoidAndSnycStatus(
        workorderid: String,
        syncStatus: Int
    ): List<WorklogEntity> {
        return worklogDaoImp.findListWorklogByWoidSyncStatus(workorderid, syncStatus)
    }

    fun prepareBodyWorklog(
        summary: String,
        description: String,
        type: String,
        wonum: String
    ): Worklog {
        val worklog = Worklog()
        worklog.description = summary
        worklog.descriptionLongdescription = description
        worklog.logtype = type
        worklog.recordkey = wonum
        worklog.createdate = DateUtils.getDateTimeMaximo()
        return worklog
    }

    fun handlingAfterUpdate(workorderid: String) {
        val worklogentity = worklogDaoImp.findListWorklogByWoid(workorderid)
        worklogentity.whatIfNotNullOrEmpty { list ->
            list.forEach {
                if (it.syncStatus?.equals(BaseParam.APP_FALSE) == true) {
                    it.syncStatus = BaseParam.APP_TRUE
                    saveWorklog(it)
                }
            }
        }
    }
}