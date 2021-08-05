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
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import id.thork.app.R
import id.thork.app.helper.builder.LocomotifAttribute
import id.thork.app.helper.builder.LocomotifBuilder
import java.util.*


class FormActivity : AppCompatActivity() {
    var locomotifBuilder: LocomotifBuilder<Person>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(1991 , 0 , 24)

        val person = Person()
        person.name = "REJA"
        person.age = 32
        person.birthDate = calendar.time
        person.address = "Puri Park View Tower E"
        person.nik = "1001"
        person.email = "rejaluo24@gmail.com"
        person.gender = "Male"

        locomotifBuilder = LocomotifBuilder(person, this)
        locomotifBuilder?.setupFields(arrayOf("birthDate","name", "address", "nik","email", "gender", "phone", "country", "city" , "zipcode", "married"))
        locomotifBuilder?.setupFieldsCaption(arrayOf("Tanggal Lahir","Nama Lengkap", "Alamat", "NIK", "Email", "Jenis Kelamin", "Phone", "Negara", "Kota", "Kode POS", "Menikah"))
        val rootView: LinearLayout = findViewById(R.id.root_view)
        locomotifBuilder?.forFieldItems("gender", getGenderItems())
        rootView.addView(locomotifBuilder?.build())

        locomotifBuilder?.setFieldReadOnlyByTag("name")
        getWidgetListener()
    }

    private fun getGenderItems(): List<LocomotifAttribute> {
        val mutableList = mutableListOf<LocomotifAttribute>()
        val male = LocomotifAttribute("Male", "Male")
        val female = LocomotifAttribute("Female", "Female")
        mutableList.add(male)
        mutableList.add(female)
        return mutableList.toList()
    }

    fun getWidgetListener() {
        val address = locomotifBuilder?.getWidgetByTag("address") as EditText
        val phone = locomotifBuilder?.getWidgetByTag("phone") as EditText
        address.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                phone.setText("Inject "  + s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    fun getData(view: View) {
        val data = locomotifBuilder?.getData()
        println("Get Data $data")
        if (data != null) {
            for ((key, value) in data) {
                println("Field $key is $value")
            }
        }

//        println(locomotifBuilder?.getDataAsJson())

        val pDuplicate: Person? = locomotifBuilder?.getDataAsEntity()
        println("RETURN AS ENTITY")
        pDuplicate.let {
            println(it?.name)
            println(it?.birthDate)
            println(it?.address)
        }
    }
}