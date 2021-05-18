package id.thork.app.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class HttpRequestInterceptor: Interceptor {
    private val TAG = HttpRequestInterceptor::class.java.name

    var url: String = ""
    var urlPort: Int = 0
    var headers: Map<String, String> = HashMap()

    fun setHost(host: String) {
        val httpUrl = host.toHttpUrlOrNull()
        this.url = httpUrl!!.host
        this.urlPort  = httpUrl.port
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
        Timber.tag(TAG).d("intercept() url: %s url port: %s", url, urlPort);
        val newUrl = originalRequest.url.newBuilder().host(url).port(urlPort).build()
        val request = originalRequest.newBuilder().url(newUrl).build()

        val requestBuilder = request.newBuilder()
        configHeader(requestBuilder, headers)
        Timber.tag(TAG).d("intercept() newUrl: %s", newUrl.toUri().toString())
        return chain.proceed(requestBuilder.build())
    }
}