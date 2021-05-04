package id.thork.app.network.model.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginCookie(
    @Json(name = "appserversecurity")
    var appserversecurity: Boolean,
    @Json(name = "inactivetimeout")
    var inactivetimeout: Int,
    @Json(name = "maxupg")
    var maxupg: String,
    @Json(name = "sessiontimeout")
    var sessiontimeout: Int
) {
    constructor() : this(
        appserversecurity = true,
        inactivetimeout = 0,
        maxupg = "",
        sessiontimeout = 0
    )
}