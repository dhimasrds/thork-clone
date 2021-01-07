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

import com.google.gson.Gson
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.initializer.ObjectBox
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.persistence.entity.UserEntity_
import io.objectbox.Box
import io.objectbox.kotlin.equal
import java.util.*

class UserDaoImp : UserDao {
    val TAG = UserDaoImp::class.java.name
    var userEntityBox: Box<UserEntity>
    var logDao: LogDaoImp
    var gson: Gson = Gson()

    init {
        userEntityBox = ObjectBox.boxStore.boxFor(UserEntity::class.java)
        logDao = LogDaoImp()
    }

    override fun createUserSession(userEntity: UserEntity, username: String): UserEntity {
        addUpdateInfo(userEntity, username)
        userEntityBox.put(userEntity)
        saveLog(userEntity, username)
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

    override fun save(userEntity: UserEntity, username: String) {
        addUpdateInfo(userEntity, username)
        userEntityBox.put(userEntity)
        saveLog(userEntity, username)
    }

    override fun delete(userEntity: UserEntity) {
        userEntityBox.remove(userEntity)
    }

    private fun saveLog(userEntity: UserEntity, username: String) {
        val jsonString = gson.toJson(userEntity, UserEntity::class.java)
        logDao.save(TAG, jsonString, username)
    }

    private fun addUpdateInfo(userEntity: UserEntity, username: String) {
        userEntity.createdBy.whatIfNotNullOrEmpty(
            whatIf = {
                userEntity.createdDate = Date()
                userEntity.createdBy = username
            }
        )
        userEntity.updatedDate = Date()
        userEntity.updatedBy = username
    }
}