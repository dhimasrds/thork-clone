package id.thork.app.network.api

import com.skydoves.sandwich.ApiResponse
import id.thork.app.network.response.google_maps.ResponseRoute
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by M.Reza Sulaiman on 18/02/21
 * Jakarta, Indonesia.
 */
interface GoogleMapsApi {
    @GET("json")
    suspend fun requestRoute (
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apikey: String
    ) : ApiResponse<ResponseRoute>
}