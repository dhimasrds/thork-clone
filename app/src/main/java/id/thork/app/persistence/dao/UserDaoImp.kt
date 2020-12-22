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

import id.thork.app.base.BaseParam
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.persistence.entity.UserEntity_
import io.objectbox.Box
import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.kotlin.equal

class UserDaoImp : UserDao {
    lateinit var userEntityLiveData: ObjectBoxLiveData<UserEntity>
    lateinit var userEntityBox: Box<UserEntity>

    override fun createUserSession(userEntity: UserEntity): ObjectBoxLiveData<UserEntity> {
        userEntityBox = ObjectBox.boxStore.boxFor(UserEntity::class.java)
        userEntityBox.put(userEntity)
        userEntityLiveData =
            ObjectBoxLiveData(userEntityBox.query().equal(UserEntity_.id, userEntity.id).build())
        return userEntityLiveData
    }

    override fun findUserByPersonUID(personUID: Int): ObjectBoxLiveData<UserEntity> {
        userEntityBox = ObjectBox.boxStore.boxFor(UserEntity::class.java)
        userEntityLiveData =
            ObjectBoxLiveData(userEntityBox.query().equal(UserEntity_.personUID, personUID).build())
        return userEntityLiveData
    }

    override fun findActiveSessionUser(): List<UserEntity> {
        userEntityBox = ObjectBox.boxStore.boxFor(UserEntity::class.java)
        return userEntityBox.query().equal(UserEntity_.session, BaseParam.APP_TRUE).build().find()
    }

    override fun save(userEntity: UserEntity) {
        userEntityBox = ObjectBox.boxStore.boxFor(UserEntity::class.java)
        userEntityBox.put(userEntity)
    }

    override fun delete(userEntity: UserEntity) {
        userEntityBox = ObjectBox.boxStore.boxFor(UserEntity::class.java)
        userEntityBox.remove(userEntity)
    }
}