package id.thork.app.repository

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.base.BaseParam
import id.thork.app.base.BaseRepository
import id.thork.app.base.MxResponse
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.api.DoclinksClient
import id.thork.app.network.api.LoginClient
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.model.user.LoginCookie
import id.thork.app.network.model.user.Logout
import id.thork.app.network.model.user.UserResponse
import id.thork.app.network.response.ErrorResponse.ErrorResponse
import id.thork.app.network.response.system_properties.SystemProperties
import id.thork.app.network.response.work_order.doclinks.DoclinksMember
import id.thork.app.persistence.dao.*
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.persistence.entity.SysPropEntity
import id.thork.app.persistence.entity.SysResEntity
import id.thork.app.persistence.entity.UserEntity
import id.thork.app.utils.DateUtils
import timber.log.Timber


class AttachmentRepository constructor(
    private val preferenceManager: PreferenceManager,
    private val attachmentDao: AttachmentDao,
    private val doclinksClient: DoclinksClient
) : BaseRepository {
    val TAG = AttachmentRepository::class.java.name

    suspend fun createAttachmentFromDocklinks(url: String, username: String) {
        var doclinksMember: DoclinksMember? = null
        getDoclinks(
            url = url,
            onSuccess = {
                doclinksMember = it
            },
            onError = {
                Timber.tag(TAG).i("loginByPerson() error: %s", it)
            }
        )
        doclinksMember.whatIfNotNull {
            createAttachment(it, username)
        }
    }

    private fun createAttachment( doclinksMember: DoclinksMember, username: String) {
        Timber.tag(TAG).i("createAttachment() doclinksMember: %s username: %s", doclinksMember, username)
        var attachmentEntities: MutableList<AttachmentEntity> = mutableListOf()
        doclinksMember.whatIfNotNull {
            it.member.whatIfNotNullOrEmpty { member ->
                member.forEach {
                    val describedBy = it.describedBy
                    describedBy.whatIfNotNull { describedBy ->
                        val modifiedDate = DateUtils.convertStringToMaximoDate(describedBy.modified)
                        Timber.tag(TAG).i("createAttachment() modifiedDate: %s", modifiedDate)
                        val attachmentEntity = AttachmentEntity(
                            docInfoId = describedBy.docinfoid, docType = describedBy.docType,
                            fileName = describedBy.fileName, mimeType = "",
                            description = describedBy.description, title = describedBy.title,
                            modifiedDate = modifiedDate, syncStatus = true,
                            uriString = describedBy.href, workOrderId = describedBy.ownerid,
                            wonum = ""
                        )
                        attachmentEntities.add(attachmentEntity)
                    }
                }
            }
        }
        attachmentDao.save(attachmentEntities, username)
    }

    private suspend fun getDoclinks(
        url: String,
        onSuccess: (DoclinksMember) -> Unit,
        onError: (String) -> Unit
    ) {
        val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
        val response = doclinksClient.getDoclinks(cookie,url + "?lean=1")
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
}