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
import id.thork.app.base.BaseParam
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.persistence.entity.UserEntity_
import io.objectbox.Box
import io.objectbox.kotlin.equal

class UserDaoImp : UserDao {
    val TAG = UserDaoImp::class.java
    var userEntityBox: Box<UserEntity>
    var logDao: LogDaoImp

    init {
        userEntityBox = ObjectBox.boxStore.boxFor(UserEntity::class.java)
        logDao = LogDaoImp()
    }

    override fun createUserSession(userEntity: UserEntity): UserEntity {
        userEntityBox.put(userEntity)
//        logDao.save()
        return userEntity
    }

    override fun findUserByPersonUID(personUID: Int): UserEntity? {
        val userEntities: List<UserEntity> =
            userEntityBox.query().equal(UserEntity_.personUID, personUID).build().find()
        userEntities.whatIfNotNullOrEmpty { return userEntities[0] }
        return null
    }

    override fun findActiveSessionUser(): UserEntity? {
        val userEntities: List<UserEntity> =
            userEntityBox.query().equal(UserEntity_.session, BaseParam.APP_TRUE).build().find()
        userEntities.whatIfNotNullOrEmpty { return userEntities[0] }
        return null
    }

    override fun save(userEntity: UserEntity) {
        userEntityBox.put(userEntity)
    }

    override fun delete(userEntity: UserEntity) {
        userEntityBox.remove(userEntity)
    }
}