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

package id.thork.app.persistence.dao

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseDao
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.NotificationEntity
import io.objectbox.Box
import java.util.*

class NotificationDaoImp : NotificationDao, BaseDao() {
    var notificationEntityBox: Box<NotificationEntity>

    init {
        notificationEntityBox = ObjectBox.boxStore.boxFor(NotificationEntity::class.java)
    }

    override fun save(
        notificationEntity: NotificationEntity,
        username: String
    ) {
        addUpdateInfo(notificationEntity, username)
        notificationEntityBox.put(notificationEntity)
    }

    override fun remove(id: Long) {
        notificationEntityBox.remove(id)
    }

    override fun findNotification(id: Long): NotificationEntity {
        return notificationEntityBox.get(id)
    }

    private fun addUpdateInfo(notificationEntity: NotificationEntity, username: String) {
        notificationEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                notificationEntity.createdDate = Date()
                notificationEntity.createdBy = username
            }
        )
        notificationEntity.updatedDate = Date()
        notificationEntity.updatedBy = username
    }

}
