package id.thork.app.pages.main.element

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.WoCacheEntity
import id.thork.app.repository.WorkOrderRepository
import id.thork.app.utils.MapsUtils
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 09/02/21
 * Jakarta, Indonesia.
 */
class MapViewModel @ViewModelInject constructor(
    private val workOrderRepository: WorkOrderRepository,
) : LiveCoroutinesViewModel() {
    val TAG = MapViewModel::class.java.name


    private val workManager = WorkManager.getInstance(context)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    private val _listWo = MutableLiveData<List<WoCacheEntity>>()

    val listWo: LiveData<List<WoCacheEntity>> get() = _listWo

    init {
        outputWorkInfos = workManager.getWorkInfosByTagLiveData("CREW_POSITION")
    }

    fun fetchListWo() {
        Timber.d("MapViewModel() fetchListWo")
        val listWoLocal = workOrderRepository.fetchWoList()
        _listWo.value = listWoLocal
    }

    fun pruneWork() {
        Timber.d("MapViewModel() pruneWork")
        workManager.pruneWork()
    }
}