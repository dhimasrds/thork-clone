package id.thork.app.network.api

import id.thork.app.network.response.work_order.Member
import id.thork.app.network.response.work_order.WorkOrderResponse
import javax.inject.Inject

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderClient @Inject constructor(
   private val workOrderApi: WorkOrderApi
) {

    suspend fun getWorkOrderList(headerParam: String, select: String, where: String,pageno : Int,pagesize : Int) =
        workOrderApi.getListWorkorder(headerParam,LEAN, select, where, pageno,pagesize)

    suspend fun searchWorkOrder(headerParam: String, select: String, where: String) =
        workOrderApi.searchWorkorder(headerParam,LEAN, select, where)

    suspend fun createWo(headerParam: String, body: Member) =
        workOrderApi.createWO(headerParam, body)

    suspend fun updateStatus(headerParam: String, xMethodeOverride: String, contentType: String, workOrderId: Int, body: Member) =
        workOrderApi.updateStatus(headerParam, xMethodeOverride, contentType, workOrderId, LEAN, body)

    companion object {
        private const val LEAN = 1
    }
}