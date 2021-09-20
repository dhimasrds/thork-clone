package id.thork.app.network.api

import id.thork.app.network.response.work_order.Labtran
import id.thork.app.network.response.work_order.Member
import javax.inject.Inject

/**
 * Created by Dhimas Saputra on 13/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderClient @Inject constructor(
    private val workOrderApi: WorkOrderApi
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

    suspend fun createWo(headerParam: String, properties: String, body: Member) =
        workOrderApi.createWO(headerParam, properties, body)

    suspend fun LocationMarker(headerParam: String, savedQuery: String, select: String) =
        workOrderApi.getLocationResource(headerParam, LEAN, savedQuery, select)

    suspend fun updateStatus(
        cookie: String,
        xMethodeOverride: String,
        contentType: String,
        patchType: String,
        workOrderId: Int,
        body: Member,
    ) =
        workOrderApi.updateStatus(
            cookie,
            xMethodeOverride,
            contentType,
            patchType,
            workOrderId,
            LEAN,
            body
        )

    suspend fun getItemMaster(headerParam: String, savedQuery: String, select: String) =
        workOrderApi.getItemMaster(headerParam, savedQuery, LEAN, select)

    suspend fun getWorklogType(headerParam: String, select: String, where: String) =
        workOrderApi.getWorklogType(headerParam, LEAN, select, where)

    suspend fun createLaborPlan(
        cookie: String,
        xMethodeOverride: String,
        contentType: String,
        patchType: String,
        properties: String,
        workOrderId: Int,
        body: Member,
    ) =
        workOrderApi.createLaborPlan(
            cookie,
            xMethodeOverride,
            contentType,
            patchType,
            properties,
            workOrderId,
            LEAN,
            body
        )

    suspend fun updateLaborPlan(
        cookie: String,
        xMethodeOverride: String,
        contentType: String,
        patchType: String,
        workOrderId: Int,
        body: Member,
    ) =
        workOrderApi.updateLaborPlan(
            cookie,
            xMethodeOverride,
            contentType,
            patchType,
            workOrderId,
            LEAN,
            body
        )

    suspend fun deleteLaborPlan(
        cookie: String,
        url : String
    ) = workOrderApi.deleteLaborPlan(cookie,url)

    suspend fun createLaborActual(
        cookie: String,
        xMethodeOverride: String,
        contentType: String,
        patchType: String,
        properties: String,
        workOrderId: Int,
        body: Member,
    ) =
        workOrderApi.createLaborActual(
            cookie,
            xMethodeOverride,
            contentType,
            patchType,
            properties,
            workOrderId,
            LEAN,
            body
        )

    suspend fun updateLaborActual(
        cookie: String,
        xMethodeOverride: String,
        contentType: String,
        patchType: String,
        labtrnasId: Int,
        body: Labtran,
    ) =
        workOrderApi.updateLaborActual(
            cookie,
            xMethodeOverride,
            contentType,
            patchType,
            labtrnasId,
            LEAN,
            body
        )



    companion object {
        private const val LEAN = 1
    }
}