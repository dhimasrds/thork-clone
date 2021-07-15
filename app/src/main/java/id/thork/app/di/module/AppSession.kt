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

package id.thork.app.di.module

import android.content.Context
import com.skydoves.whatif.whatIfNotNull
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.persistence.dao.UserDao
import id.thork.app.persistence.dao.UserDaoImp
import id.thork.app.persistence.entity.UserEntity
import timber.log.Timber
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class AppSession @Inject constructor(context: Context) {
    val TAG = AppSession::class.java.name

    var userEntity: UserEntity
    var personUID: Int = -1
    var firstLogin: Int = 1
    var userHash: String? = null
    var cookie: String? = null
    var laborCode: String? = null
    var siteId: String? = null
    var orgId: String? = null
    var serverAddress: String? = null
    var isConnected: Boolean = true

    var userDao: UserDao = UserDaoImp()

    init {
        userEntity = UserEntity()
        reinitUser()
    }

    fun reinitUser() {
        val existingUser: UserEntity? = userDao.findActiveSessionUser()
        Timber.tag(TAG).i("reinitUser() existingUser: %s", existingUser)
        existingUser.whatIfNotNull(
            whatIf = {
                userEntity = it
                userEntity.personUID.whatIfNotNull(
                    whatIf = {
                        personUID = it
                    }
                )

                userEntity.userHash.whatIfNotNull(
                    whatIf = {
                        userHash = it
                    }
                )

                userEntity.laborcode.whatIfNotNull(
                    whatIf = {
                        laborCode = it
                    }
                )

                userEntity.siteid.whatIfNotNull(
                    whatIf = {
                        siteId = it
                    }
                )

                userEntity.orgid.whatIfNotNull(
                    whatIf = {
                        orgId = it
                    }
                )

                userEntity.server_address.whatIfNotNull(
                    whatIf = {
                        serverAddress = it
                    }
                )
            }
        )
    }
}