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

import id.thork.app.persistence.entity.UserEntity
import io.objectbox.android.ObjectBoxLiveData

interface UserDao {
    fun createUserSession(userEntity: UserEntity): ObjectBoxLiveData<UserEntity>
    fun findUserByPersonUID(personUID: Int): ObjectBoxLiveData<UserEntity>
    fun findActiveSessionUser(): List<UserEntity>
    fun save(userEntity: UserEntity)
    fun delete(userEntity: UserEntity)
}