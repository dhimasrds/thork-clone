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
import com.squareup.moshi.JsonClass
import io.objectbox.annotation.Entity
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity
@JsonClass(generateAdapter = true)
data class AttachmentEntity(
    var docInfoId: Int? = null,
    var docType: String? = null,
    var fileName: String? =null,
    var mimeType: String? = null,
    var description: String? = null,
    var title: String? = null,
    var modifiedDate: Date? = null,
    var syncStatus: Boolean? = null,
    var uriString: String? = null,
    var workOrderId: Int? = null,
    var wonum: String? = null
) :BaseEntity(), Parcelable