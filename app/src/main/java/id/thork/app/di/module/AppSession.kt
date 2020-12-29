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
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.repository.LoginRepository
import timber.log.Timber
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class AppSession @Inject constructor(
    context: Context,
    private val loginRepository: LoginRepository
) {
    val TAG = AppSession::class.java.name

    var userEntity: UserEntity
    var personUID: Int = -1
    var firstLogin: Int = 1
    var userHash: String? = null
    var laborCode: String? = null
    var siteId: String? = null
    var orgId: String? = null
    var serverAddress: String? = null

    init {
        userEntity = UserEntity()
        reinitUser()
    }

    fun reinitUser() {
        val existingUser: UserEntity? = loginRepository.findActiveSession()
        Timber.tag(TAG).i("reinitUser() existingUser: %s", existingUser)
        existingUser.whatIfNotNull(
            whatIf = {
                userEntity = existingUser!!
                existingUser?.personUID.whatIfNotNull(
                    whatIf = {
                        personUID = existingUser?.personUID!!
                    }
                )

                existingUser?.userHash.whatIfNotNull(
                    whatIf = {
                        userHash = existingUser?.userHash!!
                    }
                )

                existingUser?.laborcode.whatIfNotNull(
                    whatIf = {
                        laborCode = existingUser?.laborcode!!
                    }
                )

                existingUser?.siteid.whatIfNotNull(
                    whatIf = {
                        siteId = existingUser?.siteid!!
                    }
                )

                existingUser?.orgid.whatIfNotNull(
                    whatIf = {
                        orgId = existingUser?.orgid!!
                    }
                )

                existingUser?.server_address.whatIfNotNull(
                    whatIf = {
                        serverAddress = existingUser?.server_address!!
                    }
                )

            },
        )

    }


}