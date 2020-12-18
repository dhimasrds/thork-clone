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

package id.thork.app.persistence.entity

import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.Id
import java.util.*

@BaseEntity
abstract class BaseEntity {
    @Id
    var id: Long = 0
    var createdBy: String? = null
    var createdDate: Date? = null
    var updatedBy: String? = null
    var updatedDate: Date? = null

    constructor()
}