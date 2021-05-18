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

import android.os.Parcelable
import io.objectbox.annotation.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class AttachmentEntity(
    var type: String? = null,
    var syncStatus: Boolean? = null,
    var uriString: String? = null,
    var description: String? = null,
    var name: String? = null,
    var workOrderId: Int? = null,
    var wonum: String? = null,
    var idRoot: Int? = null
) :BaseEntity(), Parcelable