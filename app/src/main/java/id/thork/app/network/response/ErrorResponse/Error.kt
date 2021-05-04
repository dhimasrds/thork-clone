package id.thork.app.network.response.ErrorResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class Error {
    @SerializedName("oslc:statusCode")
    @Expose
    var oslcStatusCode: String? = null

    @SerializedName("spi:reasonCode")
    @Expose
    var spiReasonCode: String? = null

    @SerializedName("oslc:message")
    @Expose
    var oslcMessage: String? = null

    @SerializedName("oslc:extendedError")
    @Expose
    var oslcExtendedError: ExtendedError? = null
}
