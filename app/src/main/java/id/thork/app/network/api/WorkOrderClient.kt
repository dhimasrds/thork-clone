package id.thork.app.network.api

import id.thork.app.network.response.work_order.Member
import javax.inject.Inject

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderClient @Inject constructor(
    private val workOrderApi: WorkOrderApi,
) {

    suspend fun getWorkOrderList(
        cookie: String,
        savedQuery: String,
        select: String,
        pageno: Int,
        pagesize: Int,
    ) =
        workOrderApi.getListWorkorder(cookie, LEAN, savedQuery, select, pageno, pagesize)

    suspend fun getAssetList(cookie: String, savedQuery: String, select: String) =
        workOrderApi.getListAsset(cookie, savedQuery, select)

    suspend fun searchWorkOrder(headerParam: String, select: String, where: String) =
        workOrderApi.searchWorkorder(headerParam, LEAN, select, where)

    suspend fun createWo(headerParam: String, body: Member) =
        workOrderApi.createWO(headerParam, body)


    suspend fun LocationMarker(headerParam: String, savedQuery: String, select: String) =
        workOrderApi.getLocationResource(headerParam, LEAN, savedQuery,select)

    suspend fun updateStatus(
        headerParam: String,
        xMethodeOverride: String,
        contentType: String,
        workOrderId: Int,
        body: Member,
    ) =
        workOrderApi.updateStatus(headerParam,
            xMethodeOverride,
            contentType,
            workOrderId,
            LEAN,
            body)

    companion object {
        private const val LEAN = 1
    }
}