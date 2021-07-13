package id.thork.app.repository

import android.content.Context
import android.content.Intent
import com.skydoves.sandwich.*
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.di.module.AppSession
import id.thork.app.di.module.PreferenceManager
import id.thork.app.helper.CookieHelper
import id.thork.app.helper.DoclinksParam
import id.thork.app.network.RetrofitBuilder
import id.thork.app.network.api.DoclinksApi
import id.thork.app.network.api.DoclinksClient
import id.thork.app.network.response.work_order.doclinks.DoclinksMember
import id.thork.app.persistence.dao.AttachmentDao
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.utils.DateUtils
import id.thork.app.utils.FileUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber


class AttachmentRepository constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val appSession: AppSession,
    private val attachmentDao: AttachmentDao,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
) : BaseRepository {
    val TAG = AttachmentRepository::class.java.name

    private val doclinksClient: DoclinksClient

    init {
        doclinksClient = DoclinksClient(provideDoclinksApi())
    }

    suspend fun createAttachmentFromDocklinks(url: String, username: String) {
        Timber.tag(TAG).i("createAttachmentFromDocklinks() url: %s", url)
        var doclinksMember: DoclinksMember? = null
        getDoclinks(
            url = url,
            onSuccess = {
                doclinksMember = it
            },
            onError = {
                Timber.tag(TAG).i("createAttachmentFromDocklinks() error: %s", it)
            }
        )
        doclinksMember.whatIfNotNull {
            createAttachment(it, username)
        }
    }

    private fun createAttachment(
        doclinksMember: DoclinksMember,
        username: String
    ): MutableList<AttachmentEntity> {
        Timber.tag(TAG)
            .i("createAttachment() doclinksMember: %s username: %s", doclinksMember, username)
        var attachmentEntities: MutableList<AttachmentEntity> = mutableListOf()
        doclinksMember.whatIfNotNull {
            it.member.whatIfNotNullOrEmpty { member ->
                member.forEach {
                    val describedBy = it.describedBy
                    describedBy.whatIfNotNull { describedBy ->
                        val modifiedDate = DateUtils.convertStringToMaximoDate(describedBy.modified)
                        var mimeType = BaseParam.APP_EMPTY_STRING
                        describedBy.format.whatIfNotNull { fileFormat ->
                            fileFormat.label.whatIfNotNull {
                                mimeType = it
                            }
                        }
                        Timber.tag(TAG).i(
                            "createAttachment() modifiedDate: %s mimeType: %s",
                            modifiedDate,
                            mimeType
                        )
                        val attachmentEntity = AttachmentEntity(
                            docInfoId = describedBy.docinfoid, docType = describedBy.docType,
                            fileName = describedBy.fileName, mimeType = mimeType,
                            description = describedBy.description, title = describedBy.title,
                            modifiedDate = modifiedDate, syncStatus = true,
                            uriString = it.href, workOrderId = describedBy.ownerid,
                            wonum = ""
                        )
                        attachmentEntities.add(attachmentEntity)
                    }
                }
            }
        }
        attachmentDao.save(attachmentEntities, username)
        return attachmentEntities
    }

    private suspend fun getDoclinks(
        url: String,
        onSuccess: (DoclinksMember) -> Unit,
        onError: (String) -> Unit
    ) {
        var cookie = BaseParam.APP_EMPTY_STRING
        appSession.userEntity.userHash?.let {
            cookie = CookieHelper(context, it).generateCookieIfExpired()
        }
        val response = doclinksClient.getDoclinks(cookie, url + "?lean=1")
        response.suspendOnSuccess {
            data.whatIfNotNull { response ->
                onSuccess(response)
            }
        }.onError {
            Timber.tag(TAG).i("getDoclinks() code: %s error: %s", statusCode.code, message())
            onError(statusCode.code.toString())
        }.onException {
            Timber.tag(TAG).i("getDoclinks() exception: %s", message())
            onError(message())
        }
    }

    suspend fun uploadAttachment(
        attachmentEntities: MutableList<AttachmentEntity>,
        username: String
    ) {
        runBlocking {
            launch {
                attachmentEntities.forEach { attachment ->
                    Timber.tag(TAG).d("uploadAttachment() attachment ${attachment.fileName} start")
                    uploadAttachment(attachment, username,
                        onSuccess = {
                            Timber.tag(TAG)
                                .d("uploadAttachment() attachment ${attachment.fileName} success")
                        },
                        onError = {
                            Timber.tag(TAG)
                                .d("uploadAttachment() attachment ${attachment.fileName} failed")
                        })
                }
            }
        }
    }

    suspend fun uploadAttachment(
        attachment: AttachmentEntity,
        username: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        var cookie = BaseParam.APP_EMPTY_STRING
        appSession.userEntity.userHash?.let {
            cookie = CookieHelper(context, it).generateCookieIfExpired()
        }
        val attachmentCaches: MutableList<AttachmentEntity> = mutableListOf()
        attachment.uriString?.let { uriString ->
            attachment.syncStatus?.let { syncStatus ->
                attachment.fileName?.let { fileName ->
                    Timber.tag(TAG)
                        .d("uploadAttachment() uriString: %s sync status: %s", uriString, syncStatus)
                    if (!uriString.startsWith("http") && !syncStatus) {
                        val requestBody = attachment.uriString?.let { urlString ->
                            attachment.mimeType?.let { mimeType ->
                                createRequestBody(
                                    cookie = cookie,
                                    filePath = urlString,
                                )
                            }
                        }
                        val doclinksParam = DoclinksParam(
                            cookie, attachment.workOrderId, attachment.fileName, attachment.docType,
                            attachment.description, attachment.mimeType, requestBody
                        )
                        Timber.tag(TAG).d(
                            "uploadAttachment() doclinksParam: %s requestBody:%s",
                            doclinksParam.mimeType,
                            requestBody
                        )
                        val response = doclinksClient.uploadAttachments(doclinksParam)
                        response.onSuccess {
                            Timber.tag(TAG)
                                .d("uploadAttachment() response: %s", "suspendOnSuccess")
                            attachmentCaches.add(attachment)
                        }.onError {
                            Timber.tag(TAG)
                                .d(
                                    "uploadAttachment() code: %s error: %s",
                                    statusCode.code,
                                    message()
                                )
                            onError(statusCode.code.toString())
                        }.onException {
                            Timber.tag(TAG).d("uploadAttachment() exception: %s", message())
                            onError(message())
                        }
                    }
                }
            }
        }
        updateCacheIfUploaded(attachmentCaches, username)
    }

    fun getAttachmentByWoId(woId: Int): MutableList<AttachmentEntity> {
        return attachmentDao.fetchAttachmentByWoId(woId).toMutableList()
    }

    private fun provideDoclinksApi(): DoclinksApi {
        val retrofit = RetrofitBuilder(preferenceManager, httpLoggingInterceptor).provideRetrofit()
        return retrofit.create(DoclinksApi::class.java)
    }

    fun save(attachmentEntity: AttachmentEntity, username: String) {
        attachmentDao.save(attachmentEntity, username)
    }

    private fun createRequestBody(
        cookie: String,
        filePath: String,
    ): RequestBody? {
        var requestFile: RequestBody? = FileUtils.createRequestBodyFromUri(context, filePath)
        return requestFile
    }

    private fun updateCacheIfUploaded(
        attachmentEntities: MutableList<AttachmentEntity>,
        username: String
    ) {
        attachmentEntities.forEach {
            it.syncStatus = true
        }
        attachmentDao.save(attachmentEntities, username)
    }

    fun getAttachmentByWoIdAndSyncStatus(woId: Int, syncStatus: Boolean) : MutableList<AttachmentEntity> {
        return attachmentDao.fetchAttachmentByWoIdAndSyncStatus(woId, syncStatus).toMutableList()
    }

    fun deleteAttachmentCache() {
        attachmentDao.delete()
    }
}