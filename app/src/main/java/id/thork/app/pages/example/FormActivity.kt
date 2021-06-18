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

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import id.thork.app.R
import id.thork.app.helper.builder.FormBuilder
import java.util.*


class FormActivity : AppCompatActivity() {
    var formBuilder: FormBuilder<Person>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(1991 , 0 , 24)

        val person = Person()
        person.name = "REJA"
        person.age = 32
        person.birthDate = calendar.time

        formBuilder = FormBuilder(person, this)
        formBuilder?.setupFields(arrayOf("birthDate","name"))
        val rootView: LinearLayout = findViewById(R.id.root_view)
        rootView.addView(formBuilder?.build())
    }

    fun getData(view: View) {
        val data = formBuilder?.getData()
        println("Get Data $data")
        if (data != null) {
            for ((key, value) in data) {
                println("Field $key is $value")
            }
        }
    }
}