package id.thork.app.network.response.ErrorResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class Error {
    @SerializedName("statusCode")
    @Expose
    var oslcStatusCode: String? = null

    @SerializedName("reasonCode")
    @Expose
    var spiReasonCode: String? = null

    @SerializedName("message")
    @Expose
    var oslcMessage: String? = null

    @SerializedName("extendedError")
    @Expose
    var oslcExtendedError: ExtendedError? = null
}
