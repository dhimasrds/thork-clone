package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.base.BaseParam
import id.thork.app.network.response.firebase.ResponseFirebase
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created by M.Reza Sulaiman on 20/04/21
 * Jakarta, Indonesia.
 */
interface FirebaseApi {
    @POST("/fcm/send")
    suspend fun updateCrewPosition(
        @Header("authorization") authorization: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Body data: RequestBody
    ): ApiResponse<ResponseFirebase>
}