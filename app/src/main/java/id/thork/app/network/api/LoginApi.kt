package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.network.model.Todo
import id.thork.app.network.model.user.LoginCookie
import id.thork.app.network.model.user.Logout
import id.thork.app.network.model.user.UserResponse
import retrofit2.http.*

interface LoginApi {
    /**
     * Login API used for application login
     */
    @GET("maximo/oslc/os/thisfsmusers")
    suspend fun loginByPerson(
        @Query("lean") lean: Int = 1,
        @Query("oslc.select") select: String,
        @Query("oslc.where") where: String
    ): ApiResponse<UserResponse>

    @POST("maximo/oslc/login")
    suspend fun login(
        @Header("maxauth") maxAuth: String?,
    ): ApiResponse<LoginCookie>

    @POST("maximo/oslc/logout")
    suspend fun logout(
        @Header("Cookie") cookie: String?,
        @Header("maxauth") maxAuth: String?,
    ): ApiResponse<Logout>

    @GET("/todos/{id}")
    suspend fun getTodo(@Path("id") id: Int): Todo

    @GET("/maximo/oslc/os/thisfsmapp")
    suspend fun getSystemProperties(
        @Header("maxauth") maxAuth: String?,
        @Query("lean") lean: Int = 1,
        @Query("oslc.select") select: String
    ) : ApiResponse<id.thork.app.network.response.system_properties.SystemProperties>
}