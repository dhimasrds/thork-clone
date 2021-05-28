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

import io.objectbox.annotation.Entity

@Entity
class MaterialEntity(
    var itemNum: String? = null,
    var itemType: String? = null,
    var itemSetId: String? = null,
    var description: String? = null,
    var lotType: String? = null,
    var lotTypeDescription: String? = null,
    var thisfsmrfid: String? = null
) : BaseEntity()