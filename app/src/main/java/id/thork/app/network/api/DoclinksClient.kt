package id.thork.app.network.api

import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Reja on 24/05/21
 * Jakarta, Indonesia.
 */
class DoclinksClient @Inject constructor(
    @Named("docklinksApiGlobal")
    private val doclinksApi: DoclinksApi
) {

    suspend fun getDoclinks(cookie: String, url: String) =
        doclinksApi.getDoclinks(cookie, url)
}