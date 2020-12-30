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

interface UserDao {
    fun createUserSession(userEntity: UserEntity,username: String): UserEntity
    fun findUserByPersonUID(personUID: Int): UserEntity?
    fun findActiveSessionUser(): UserEntity?
    fun save(userEntity: UserEntity, username: String)
    fun delete(userEntity: UserEntity)
}