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

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.thork.app.persistence.dao.AssetDao
import id.thork.app.persistence.dao.AssetDaoImp
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.dao.WoCacheDaoImp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {
    private val TAG = CacheModule::class.java.name

    @Singleton
    @Provides
    fun provideWoCacheDao(): WoCacheDao {
        return WoCacheDaoImp()
    }

    @Singleton
    @Provides
    fun provideAssetDao(): AssetDao {
        return AssetDaoImp()
    }
}