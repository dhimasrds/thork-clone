package id.thork.app.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class HttpRequestInterceptor: Interceptor {
    var url: String = "149.129.252.41"
    var urlPort: Int = 9080
    var headers: Map<String, String> = HashMap()

    fun setHost(url: String) {
        this.url = url
    }

    fun setPort(urlPort: Int) {
        this.urlPort = urlPort
    }

    fun addHeader(headers: Map<String, String>) {
        this.headers = headers
    }

    private fun configHeader(builder:Request.Builder, headers: Map<String, String>) {
        for ((key,value) in headers) {
            builder.header(key, value)
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newUrl = originalRequest.url.newBuilder().host(url)
            .port(urlPort).build()
        val request = originalRequest.newBuilder().url(newUrl).build()

        var requestBuilder = request.newBuilder()
        configHeader(requestBuilder, headers)
        Timber.d(requestBuilder.toString())
        return chain.proceed(requestBuilder.build())
    }
}