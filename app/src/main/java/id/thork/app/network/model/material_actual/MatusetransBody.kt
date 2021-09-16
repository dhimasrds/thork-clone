package id.thork.app.network.model.material_actual


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MatusetransBody(
    @Json(name = "matusetrans")
    var matusetrans: List<Matusetran>? = null
)