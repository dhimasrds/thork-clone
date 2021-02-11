package id.thork.app.pages.detail_wo.element

import androidx.hilt.lifecycle.ViewModelInject
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.repository.WorkOrderRepository

/**
 * Created by M.Reza Sulaiman on 11/02/21
 * Jakarta, Indonesia.
 */
class DetailWoViewModel@ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
    private val appSession: AppSession
)
    : LiveCoroutinesViewModel() {
}