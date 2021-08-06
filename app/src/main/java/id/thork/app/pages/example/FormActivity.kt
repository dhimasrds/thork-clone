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

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.skydoves.whatif.whatIfNotNull
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import id.thork.app.R
import id.thork.app.base.BaseParam
import id.thork.app.helper.builder.LocomotifAttribute
import id.thork.app.helper.builder.LocomotifBuilder
import id.thork.app.pages.followup_wo.FollowUpWoActivity
import timber.log.Timber
import java.util.*


class FormActivity : AppCompatActivity() {
    var locomotifBuilder: LocomotifBuilder<Person>? = null

    lateinit var country: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(1991, 0, 24)

        val person = Person()
        person.whatIfNotNull { person ->
            locomotifBuilder = LocomotifBuilder(person, this)
            locomotifBuilder?.setupFields(
                arrayOf(
                    "birthDate",
                    "name",
                    "address",
                    "nik",
                    "email",
                    "gender",
                    "phone",
                    "country",
                    "city",
                    "zipcode",
                    "married"
                )
            )
            locomotifBuilder?.setupFieldsCaption(
                arrayOf(
                    "Tanggal Lahir",
                    "Nama Lengkap",
                    "Alamat Rumah",
                    "NIK",
                    "Email",
                    "Jenis Kelamin",
                    "Phone",
                    "Negara",
                    "Kota",
                    "Kode POS",
                    "Menikah"
                )
            )
            val rootView: LinearLayout = findViewById(R.id.root_view)
            locomotifBuilder?.forFieldItems("gender", getGenderItems())
            locomotifBuilder?.forFieldItems("city", getCities())
            rootView.addView(locomotifBuilder?.build())

            getWidgetListener()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.tag("TAG").d("onActivityResult() resultcode: %s", resultCode)
        if (resultCode == RESULT_OK) {
            if (requestCode == 123) {
                country.setText(data?.getStringExtra("NAME"))
            }
        }
    }

    private fun getGenderItems(): List<LocomotifAttribute> {
        val mutableList = mutableListOf<LocomotifAttribute>()
        val male = LocomotifAttribute("Male", "Male")
        val female = LocomotifAttribute("Female", "Female")
        val malefemale = LocomotifAttribute("MaleFemale", "MaleFemale")
        mutableList.add(male)
        mutableList.add(female)
        mutableList.add(malefemale)
        return mutableList.toList()
    }

    private fun getCities(): List<LocomotifAttribute> {
        val mutableList = mutableListOf<LocomotifAttribute>()
        val newYork = LocomotifAttribute("New York", "New York")
        val jakarta = LocomotifAttribute("Jakarta", "Jakarta")
        val macao = LocomotifAttribute("Macao", "Macao")
        mutableList.add(newYork)
        mutableList.add(jakarta)
        mutableList.add(macao)
        return mutableList.toList()
    }

    private fun getWidgetListener() {
        country = locomotifBuilder?.getWidgetByTag("country") as EditText
        country.setOnClickListener {
            Timber.tag("TAG").d("getWidgetListener() country listener")
            val intent = Intent(this, CountryListActivity::class.java)
            startActivityForResult(intent, 123)
        }

        val address = locomotifBuilder?.getWidgetByTag("address") as EditText
        val phone = locomotifBuilder?.getWidgetByTag("phone") as EditText
        address.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                phone.setText("")
                locomotifBuilder?.setFieldReadOnlyByTag("name")
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

        val pDuplicate: Person? = locomotifBuilder?.getDataAsEntity()
        println("RETURN AS ENTITY")
        pDuplicate.let {
            println(it?.name)
            println(it?.birthDate)
            println(it?.address)
        }
    }
}