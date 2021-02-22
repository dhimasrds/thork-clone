package id.thork.app.pages.main.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.WorkOrderRepository
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 09/02/21
 * Jakarta, Indonesia.
 */
class MapViewModel @ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
): LiveCoroutinesViewModel() {

    private val _listWo = MutableLiveData<List<WoCacheEntity>>()

    val listWo: LiveData<List<WoCacheEntity>> get() = _listWo

    fun fetchListWo() {
        Timber.d("MapViewModel() fetchListWo")
        val listWoLocal = workOrderRepository.fetchWoList()
        _listWo.value = listWoLocal
    }

}