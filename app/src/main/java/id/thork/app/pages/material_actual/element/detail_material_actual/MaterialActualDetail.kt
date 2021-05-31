package id.thork.app.pages.material_actual.element.detail_material_actual

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMaterialActualDetailBinding
import id.thork.app.databinding.ActivityMaterialActualFormBinding
import id.thork.app.pages.material_actual.MaterialActualActivity
import id.thork.app.pages.material_actual.element.form.MaterialActualFormActivity
import id.thork.app.pages.material_actual.element.form.MaterialActualFormViewModel
import id.thork.app.persistence.entity.MaterialEntity
import id.thork.app.persistence.entity.MatusetransEntity
import timber.log.Timber

class MaterialActualDetail : BaseActivity(){
    val TAG = MaterialActualDetail::class.java.name

    val viewModel: MaterialActualDetailViewModel by viewModels()
    private val binding: ActivityMaterialActualDetailBinding by binding(R.layout.activity_material_actual_detail)
    var intentWorkorderId: String? = null
    private var matusetransEntity: MatusetransEntity? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@MaterialActualDetail
            vm = viewModel
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.wo_material_actual),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false
        )
        retrieveFromIntent()
    }

    private fun retrieveFromIntent() {
        val intentWoId = intent.getStringExtra(BaseParam.WORKORDERID)
        val intentItemnum = intent.getStringExtra(BaseParam.MATERIAL)
        Timber.d("retrieveFromIntent() intentWoId: %s", intentWoId)
        intentWoId.whatIfNotNull {
            intentWorkorderId = it
            intentItemnum.whatIfNotNull {
                viewModel.checkResultMaterial(it, intentWorkorderId.toString())
            }
        }
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.materialCache.observe(this, Observer {
            binding.etMaterial.setText(it.itemNum)
            binding.includeMaterialPlanForm.etDescription.setText(it.description)
            binding.includeMaterialPlanForm.etQty.setText(it.itemqty.toString())
            matusetransEntity = it
        })

        viewModel.result.observe(this, Observer {
            if (it == BaseParam.APP_TRUE) {
                gotoMaterialActual()
            }
        })
    }

    override fun setupListener() {
        super.setupListener()

        binding.btnSave.setOnClickListener {
            //TODO Save Record
            matusetransEntity.whatIfNotNull {
                viewModel.saveMaterialCache(
                    it,
                    binding.includeMaterialPlanForm.etQty.text.toString()
                )
            }
        }
    }

    override fun goToPreviousActivity() {
        super.goToPreviousActivity()
        gotoMaterialActual()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        gotoMaterialActual()
    }

    private fun gotoMaterialActual() {
        val intent = Intent(this, MaterialActualActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, intentWorkorderId?.toInt())
        startActivity(intent)
        finish()
    }




}