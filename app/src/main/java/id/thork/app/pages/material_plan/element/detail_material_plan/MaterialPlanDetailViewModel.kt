package id.thork.app.pages.material_plan.element.detail_material_plan

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.LiveCoroutinesViewModel
import id.thork.app.pages.material_plan.element.form.MaterialPlanFormViewModel
import id.thork.app.persistence.entity.WpmaterialEntity
import id.thork.app.repository.MaterialRepository

/**
 * Created by M.Reza Sulaiman on 30/05/2021
 * Jakarta, Indonesia.
 */
class MaterialPlanDetailViewModel @ViewModelInject constructor(
    private val context: Context,
    private val materialRepository: MaterialRepository
) : LiveCoroutinesViewModel() {

    private val TAG = MaterialPlanFormViewModel::class.java.name

    private val _wpmaterialCache = MutableLiveData<WpmaterialEntity>()
    val wpmaterialCache: LiveData<WpmaterialEntity> get() = _wpmaterialCache

    fun checkResultMaterial(itemnum: String, workorderid: String) {
        val materialCache = materialRepository.getMaterialPlanByWoidAndItemnum(workorderid, itemnum)
        materialCache.whatIfNotNull {
            _wpmaterialCache.value = it
        }
    }
}