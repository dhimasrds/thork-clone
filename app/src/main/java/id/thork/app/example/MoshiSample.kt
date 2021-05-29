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

package id.thork.app.example

import com.squareup.moshi.*
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import id.thork.app.utils.MoshiUtils
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

/*
This is Moshi Documentation how to use Moshi for
1. Convert Object to JSON Object and JSON Array
2. Convert JSON Object or JSON Array into Kotlin Object
 */

fun main() {
    println("Moshi Sample")

//    createPersonObjectFromJson()
//    createPersonArrayFromJson()
//
//    createJsonFromObject()
//    createJsonArrayFromObject()

    //convertMapToJson()
    createJsonEmptyField()
}

fun createJsonEmptyField() {
    val person = Person(name = "Sherlock Holmes")

    val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
    val jsonAdapter: JsonAdapter<Person> = moshi.adapter(Person::class.java)

    val jsonString = jsonAdapter.toJson(person)
    println("Json String: $jsonString")
    println()
}

fun createPersonObjectFromJson() {
    println("createPersonObjectFromJson")
    val personJson = "{\"name\":\"Sherlock Holmes\", \"address\":\"Baker Street 588\",\"age\":25}"

    val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
    val jsonAdapter: JsonAdapter<Person> = moshi.adapter(Person::class.java)
    val person = jsonAdapter.fromJson(personJson)
    println("JSON Object: $personJson")
    println("Person name: ${person?.name}")
    println()
}

fun createPersonArrayFromJson() {
    println("createPersonArrayFromJson")
    val personJsonArray =
        "[{\"name\":\"Sherlock Holmes\", \"address\":\"Baker Street 588\",\"age\":25},\n" +
                "{\"name\":\"John Watson\", \"address\":\"Baker Street 588\",\"age\":24},\n" +
                "{\"name\":\"Irene Adler\", \"address\":\"Belgravia\",\"age\":26}]"

    val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
    val type: Type = Types.newParameterizedType(List::class.java, Person::class.java)
    val jsonAdapter: JsonAdapter<List<Person>> = moshi.adapter(type)
    val persons = jsonAdapter.fromJson(personJsonArray)

    println("JSON Array: $personJsonArray")
    if (persons != null) {
        var i = 1
        for (person in persons) {
            println("Person $i name is ${person.name}")
            i++
        }
    }
    println()
}

fun createJsonFromObject() {
    println("createJsonFromObject")
    val person = Person("Sherlock Holmes", "Baker Street 588", 25)

    val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
    val jsonAdapter: JsonAdapter<Person> = moshi.adapter(Person::class.java)

    val jsonString = jsonAdapter.toJson(person)
    println("Json String: $jsonString")
    println()
}

fun createJsonArrayFromObject() {
    println("createJsonArrayFromObject")
    val personList: MutableList<Person> = ArrayList()
    personList.add(Person("Sherlock Holmes", "Baker Street 588", 25))
    personList.add(Person("John Watson", "Baker Street 588", 24))
    personList.add(Person("Irene Adler", "Belgravia", 26))

    val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
    val type: Type = Types.newParameterizedType(List::class.java, Person::class.java)
    val jsonAdapter: JsonAdapter<List<Person>> = moshi.adapter(type)
    val jsonString = jsonAdapter.toJson(personList)
    println("Json Array String: $jsonString")
    println()
}

fun convertMapToJson() {
    val map = mutableMapOf("name" to "reja", "id" to "123")
    val json = MoshiUtils.mapToJson(map)
    println(json)
}

@JsonClass(generateAdapter = true)
data class Person(
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "address")
    val address: String? = null,
    @Json(name = "age")
    val age: Int? = null,
    @Transient
    val newVariable1: String? = null
)


