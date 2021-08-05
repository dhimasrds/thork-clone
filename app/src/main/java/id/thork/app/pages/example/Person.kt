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

package id.thork.app.pages.example

import id.thork.app.helper.builder.LocoCheckBox
import id.thork.app.helper.builder.LocoLov
import id.thork.app.helper.builder.LocoRadioButton
import java.util.*

class Person {
    var id: Long = 0
    var name: String? = null
    var address: String? = null
    var nik: String? = null
    var email: String? = null
    var phone: String? = null
    var birthDate: Date? = null
    var age: Int? = null
    @LocoLov
    var country: String? = null
    var city: String? = null
    var state: String? = null
    var zipcode: String? = null

    @LocoRadioButton
    var gender: String? = null

    @LocoCheckBox
    var married: Boolean = false
    constructor()
}