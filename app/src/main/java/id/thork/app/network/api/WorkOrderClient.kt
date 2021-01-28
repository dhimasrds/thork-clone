package id.thork.app.network.api

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

    companion object {
        private const val LEAN = 1
    }
}