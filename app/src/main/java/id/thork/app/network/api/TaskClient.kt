package id.thork.app.network.api

import id.thork.app.network.response.task_response.TaskResponse
import javax.inject.Inject

/**
 * Created by Raka Putra on 7/5/21
 * Jakarta, Indonesia.
 */
class TaskClient @Inject constructor(
    private val taskApi: TaskApi
) {
    suspend fun createTask(
        xMethodOverride: String?,
        contentType: String,
        cookie: String,
        patchType: String,
        properties: String,
        woid: Int,
        body: TaskResponse
    ) = taskApi.createTask(xMethodOverride, contentType, cookie, patchType, properties,  woid, LEAN, body)

    suspend fun editTask(
        xMethodOverride: String?,
        contentType: String,
        cookie: String,
        patchType: String,
        properties: String,
        woid: Int,
        body: TaskResponse
    ) = taskApi.editTask(xMethodOverride, contentType, cookie, patchType, properties,  woid, LEAN, body)


    companion object {
        private const val LEAN = 1
    }
}