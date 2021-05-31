package id.thork.app.pages.material_actual.element.material_actual_item_master

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.repository.MaterialRepository

/**
 * Created by M.Reza Sulaiman on 30/05/2021
 * Jakarta, Indonesia.
 */
class MaterialActualItemViewModel @ViewModelInject constructor(
    private val context: Context,
    private val materialRepository: MaterialRepository
) : LiveCoroutinesViewModel() {
    val TAG = MaterialActualItemViewModel::class.java.name

    private val _listMaterial = MutableLiveData<List<MaterialEntity>>()

    val listMaterial: LiveData<List<MaterialEntity>> get() = _listMaterial


    fun initListMaterial() {
        val materialPlan = materialRepository.getListItemMaster()
        materialPlan.whatIfNotNull {
            _listMaterial.value = it
        }
    }
}