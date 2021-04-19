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
class NotificationEntity : BaseEntity {
    var message: String? = null
    var trxId: String? = null
    var trxNumber: String? = null
    var notificationId: String? = null
    var type: String? = null

    constructor()

    constructor(
        message: String?,
        trxId: String?,
        trxNumber: String?,
        notificationId: String?,
        type: String?
    ) : super() {
        this.message = message
        this.trxId = trxId
        this.trxNumber = trxNumber
        this.notificationId = notificationId
        this.type = type
    }

}