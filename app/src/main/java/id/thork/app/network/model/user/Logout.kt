package id.thork.app.network.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Raka Putra on 4/29/21
 * Jakarta, Indonesia.
 */

@JsonClass(generateAdapter = true)
data class Logout (
    @Json(name = "appserversecurity")
    val appserversecurity: Boolean? = null
)