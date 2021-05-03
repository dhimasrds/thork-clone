package id.thork.app.network.response.ErrorResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ExtendedError {
    @SerializedName("oslc:moreInfo")
    @Expose
    var oslcMoreInfo: MoreInfo? = null
}