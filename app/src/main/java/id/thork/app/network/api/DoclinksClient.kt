package id.thork.app.network.api

import id.thork.app.helper.DoclinksParam
import javax.inject.Inject

/**
 * Created by Reja on 24/05/21
 * Jakarta, Indonesia.
 */
class DoclinksClient @Inject constructor(
    private val doclinksApi: DoclinksApi
) {

    suspend fun getDoclinks(cookie: String, url: String) =
        doclinksApi.getDoclinks(cookie, url)

    suspend fun uploadAttachments(param: DoclinksParam) =
        doclinksApi.uploadAttachment(param.woId!!, param.cookie, param.fileName,
            param.fileType, param.description, param.mimeType, param.requestBody!!)
}