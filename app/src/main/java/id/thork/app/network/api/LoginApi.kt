package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.base.BaseParam
import id.thork.app.network.model.Todo
import id.thork.app.network.model.user.UserResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface LoginApi {
    /**
     * Login API used for application login
     */
    @GET("maximo/oslc/os/oslcperson")
    suspend fun loginByPerson(
        @Header(BaseParam.APP_MAX_AUTH) maxAuth: String,
        @Query("lean") lean: Int = 1,
        @Query("oslc.select") select: String,
        @Query("oslc.where") where: String
    ): ApiResponse<UserResponse>

    @GET("/todos/{id}")
    suspend fun getTodo(@Path("id") id: Int): Todo
}