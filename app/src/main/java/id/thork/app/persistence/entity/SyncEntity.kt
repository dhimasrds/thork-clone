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
import java.util.*

@Entity
class SyncEntity: BaseEntity {
    var trxId: String? = null
    var message: String? = null
    var syncSuccess: Boolean = false
    var syncDate: Date? = null

    constructor()
    constructor(trxId: String?, message: String?) : super() {
        this.trxId = trxId
        this.message = message
    }

    constructor(trxId: String?, message: String?, syncSuccess: Boolean) : super() {
        this.trxId = trxId
        this.message = message
        this.syncSuccess = syncSuccess
    }

    constructor(trxId: String?, message: String?, syncSuccess: Boolean, syncDate: Date?) : super() {
        this.trxId = trxId
        this.message = message
        this.syncSuccess = syncSuccess
        this.syncDate = syncDate
    }


}