package id.thork.app.repository

import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseRepository
import id.thork.app.network.api.WorkOrderClient
import id.thork.app.network.response.work_order.WorkOrderResponse
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderRepository constructor(
    private val workOrderClient: WorkOrderClient
) : BaseRepository {
    val TAG = WorkOrderRepository::class.java.name

    suspend fun getWorkOrderList(
        headerParam: String,
        select: String,
        where: String,
        onSuccess: (WorkOrderResponse) -> Unit,
        onError: (String) -> Unit
    ){
        val response = workOrderClient.getWorkOrderList(headerParam,select,where)
        response.suspendOnSuccess {
            data.whatIfNotNull {response->
                //TODO
                //Save user session into local cache
                onSuccess(response)
            }
        }
            .onError {
                Timber.tag(TAG).i("loginByPerson() code: %s error: %s", statusCode.code, message())
                onError(message())
            }
            .onException {
                Timber.tag(TAG).i("loginByPerson() exception: %s", message())
                onError(message())
            }
    }
}