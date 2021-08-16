package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.LaborPlanEntity
import id.thork.app.persistence.entity.LaborPlanEntity_
import io.objectbox.Box
import timber.log.Timber
import java.util.*

/**
 * Created by M.Reza Sulaiman on 29/07/2021
 * Jakarta, Indonesia.
 */
class LaborPlanDaoImp : LaborPlanDao {
    val TAG = LaborPlanDaoImp::class.java.name

    var laborPlanEntityBox: Box<LaborPlanEntity>

    init {
        laborPlanEntityBox = ObjectBox.boxStore.boxFor(LaborPlanEntity::class.java)
    }

    private fun addUpdateInfo(laborPlanEntity: LaborPlanEntity, username: String?) {
        laborPlanEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                laborPlanEntity.createdDate = Date()
                laborPlanEntity.createdBy = username
            }
        )
        laborPlanEntity.updatedDate = Date()
        laborPlanEntity.updatedBy = username
    }

    override fun remove() {
        laborPlanEntityBox.removeAll()
    }

    override fun createLaborPlanCache(laborPlanEntity: LaborPlanEntity, username: String?) {
        addUpdateInfo(laborPlanEntity, username)
        laborPlanEntityBox.put(laborPlanEntity)

    }

    override fun findlaborPlanByworkorderid(
        laborcode: String,
        workorderid: String
    ): LaborPlanEntity? {
        val laborPlanEntity =
            laborPlanEntityBox.query().equal(LaborPlanEntity_.laborcode, laborcode)
                .equal(LaborPlanEntity_.workorderid, workorderid).build()
                .find()
        laborPlanEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findlaborPlanByworkorderidandCraft(
        craft: String,
        workorderid: String
    ): LaborPlanEntity? {
        val laborPlanEntity =
            laborPlanEntityBox.query().equal(LaborPlanEntity_.craft, craft)
                .equal(LaborPlanEntity_.workorderid, workorderid).build()
                .find()
        laborPlanEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findlaborPlanBylaborcodedandCraft(
        laborcode: String,
        craft: String
    ): LaborPlanEntity? {
        val laborPlanEntity =
            laborPlanEntityBox.query().equal(LaborPlanEntity_.laborcode, laborcode)
                .equal(LaborPlanEntity_.craft, craft).build()
                .find()
        laborPlanEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }



    override fun findListLaborPlan(workorderid: String): List<LaborPlanEntity> {
        return laborPlanEntityBox.query()
            .equal(LaborPlanEntity_.workorderid, workorderid)
            .build().find()
    }


    override fun findListLaborPlanlbyTaskid(workorderid: String, taskid: String) : List<LaborPlanEntity> {
        return laborPlanEntityBox.query().equal(LaborPlanEntity_.workorderid, workorderid).equal(LaborPlanEntity_.taskid, taskid).
        build().find()
    }

    override fun findlaborPlanByworkorderidAndTask(
        laborcode: String,
        wonum: String,
        taskid: String
    ): LaborPlanEntity? {
        val laborPlanEntity =
            laborPlanEntityBox.query().equal(LaborPlanEntity_.laborcode, laborcode)
                .equal(LaborPlanEntity_.wonumHeader, wonum)
                .equal(LaborPlanEntity_.taskid, taskid)
                .build()
                .find()
        laborPlanEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }

    override fun findListLaborPlanlbyWonumAndTaskid(wonum: String, taskid: String) : List<LaborPlanEntity> {
        return laborPlanEntityBox.query().equal(LaborPlanEntity_.wonumHeader, wonum).equal(LaborPlanEntity_.taskid, taskid).
        build().find()
    }

    override fun findlaborPlanBycraftAndTask(
        craft: String,
        wonum: String,
        taskid: String
    ): LaborPlanEntity? {
        val laborPlanEntity =
            laborPlanEntityBox.query().equal(LaborPlanEntity_.craft, craft)
                .equal(LaborPlanEntity_.wonumHeader, wonum)
                .equal(LaborPlanEntity_.taskid, taskid)
                .build()
                .find()
        laborPlanEntity.whatIfNotNullOrEmpty {
            return it[0]
        }
        return null
    }






}