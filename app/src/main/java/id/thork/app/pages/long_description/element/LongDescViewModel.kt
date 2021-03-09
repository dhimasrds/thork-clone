package id.thork.app.pages.long_description.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.di.module.AppSession
import id.thork.app.network.response.work_order.Member
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.utils.WoUtils

/**
 * Created by Raka Putra on 3/8/21
 * Jakarta, Indonesia.
 */
class LongDescViewModel @ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
    private val appSession: AppSession
) : LiveCoroutinesViewModel() {

    private val _CurrentMember = MutableLiveData<Member>()
    private val _username = MutableLiveData<String>()
    val CurrentMember: LiveData<Member> get() = _CurrentMember
    val username: LiveData<String> get() = _username


    fun fetchWobyWonum(wonum: String) {
        val woCacheEntity: WoCacheEntity? = workOrderRepository.findWobyWonum(wonum)
        val member = WoUtils.convertBodyToMember(woCacheEntity!!.syncBody.toString())
        _CurrentMember.value = member
    }

    fun validateUsername() {
        _username.value = appSession.userEntity.laborcode
    }

    fun findWobyWonum(wonum: String): WoCacheEntity? {
        return workOrderRepository.findWobyWonum(wonum)
    }

    fun updateWo(woCacheEntity: WoCacheEntity, username: String){
        workOrderRepository.updateWo(woCacheEntity, username)
    }
}