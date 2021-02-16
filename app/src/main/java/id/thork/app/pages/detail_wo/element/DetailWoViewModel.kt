package id.thork.app.pages.detail_wo.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.work_order.Member
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.WorkOrderRepository

/**
 * Created by M.Reza Sulaiman on 11/02/21
 * Jakarta, Indonesia.
 */
class DetailWoViewModel @ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {

    private val _CurrentMember = MutableLiveData<Member>()

    val CurrentMember: LiveData<Member> get() = _CurrentMember

    fun fetchWobyWonum(wonum: String) {
        val woCacheEntity: WoCacheEntity? = workOrderRepository.findWobyWonum(wonum)
        convertBodyToMember(woCacheEntity!!.syncBody.toString())
    }

    private fun convertBodyToMember(syncBody: String) {
        val moshi = Moshi.Builder().build()
        val memberJsonAdapter: JsonAdapter<Member> = moshi.adapter<Member>(
            Member::class.java
        )
        val member: Member? = memberJsonAdapter.fromJson(syncBody)
        _CurrentMember.value = member
    }
}