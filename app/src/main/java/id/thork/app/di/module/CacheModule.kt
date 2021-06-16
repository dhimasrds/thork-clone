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
import id.thork.app.persistence.dao.*
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
    fun provideMultiAssetDao(): MultiAssetDao {
        return MultiAssetDaoImp()
    }

    @Singleton
    @Provides
    fun providelocationDao(): LocationDao {
        return LocationDaoImp()
    }

    @Singleton
    @Provides
    fun provideAssetDao(): AssetDao {
        return AssetDaoImp()
    }

    @Singleton
    @Provides
    fun provideAttachmentDao(): AttachmentDao {
        return AttachmentDaoImp()
    }

    @Singleton
    @Provides
    fun provideMaterialBackupDao(): MaterialBackupDao {
        return MaterialBackupDaoImp()
    }

    @Singleton
    @Provides
    fun provideMaterialDao(): MaterialDao {
        return MaterialDaoImp()
    }

    @Singleton
    @Provides
    fun provideWpmaterialDao(): WpmaterialDao {
        return WpmaterialDaoImp()
    }

    @Singleton
    @Provides
    fun provideMatusetransDao(): MatusetransDao {
        return MatusetransDaoImp()
    }

    @Singleton
    @Provides
    fun provideWorklogDao(): WorklogDao {
        return WorklogDaoImp()
    }

    @Singleton
    @Provides
    fun provideWorklogType(): WorklogTypeDao {
        return WorklogTypeDaoImp()
    }

    @Singleton
    @Provides
    fun provideAttendanceDao(): AttendanceDao {
        return AttendanceDaoImp()
    }

}