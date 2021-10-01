/*
 * Copyright (c) 2019 by This.ID, Indonesia . All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * This.ID. ("Confidential Information").
 *
 * Such Confidential Information shall not be disclosed and shall
 * use it only	 in accordance with the terms of the license agreement
 * entered into with This.ID; other than in accordance with the written
 * permission of This.ID.
 */

package id.thork.app.base

import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.dao.WoCacheDaoImp
import id.thork.app.persistence.entity.BaseEntity
import timber.log.Timber
import java.util.*

abstract class BaseDao {

    var woCacheDao: WoCacheDao = WoCacheDaoImp()
    private val TAG = BaseDao::class.java.name

    protected fun addUpdateInfo(baseEntity: BaseEntity, username: String?) {
        baseEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                Timber.tag(TAG).d("addUpdateInfo() update entity")
            },
            whatIfNot = {
                baseEntity.createdDate = Date()
                baseEntity.createdBy = username
            }
        )
        baseEntity.updatedDate = Date()
        baseEntity.updatedBy = username
    }

    protected fun updateChangeDateWo(woid: Int, username: String?) {
        val wo = woCacheDao.findWoByWoId(woid)
        wo.whatIfNotNull {
            it.changeDateLocal = Date()

            woCacheDao.createWoCache(it, username)
        }
    }
}