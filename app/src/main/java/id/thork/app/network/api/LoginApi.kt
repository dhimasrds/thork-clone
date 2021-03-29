package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.base.BaseParam
import id.thork.app.network.model.Todo
import id.thork.app.network.model.user.ResponseApiKey
import id.thork.app.network.model.user.TokenApikey
import id.thork.app.network.model.user.UserResponse
import retrofit2.http.*

interface LoginApi {
    /**
     * Login API used for application login
     */
    @GET("maximo/oslc/os/oslcperson")
    suspend fun loginByPerson(
        @Header("apikey") apikey: String,
        @Query("lean") lean: Int = 1,
        @Query("oslc.select") select: String,
        @Query("oslc.where") where: String
    ): ApiResponse<UserResponse>

    @POST("maximo/oslc/apitoken/create")
    suspend fun createTokenApi(
        @Header("maxauth") maxAuth: String?,
        @Header(BaseParam.APP_CONTENT_TYPE) contentType: String?,
        @Body body: TokenApikey
    ): ApiResponse<ResponseApiKey>

    @GET("/todos/{id}")
    suspend fun getTodo(@Path("id") id: Int): Todo
}