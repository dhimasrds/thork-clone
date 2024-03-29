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

package id.thork.app.base

import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.persistence.dao.WpmaterialDaoImp
import id.thork.app.persistence.entity.BaseEntity
import timber.log.Timber
import java.util.*

abstract class BaseRepository {
    private val TAG = BaseRepository::class.java.name
}