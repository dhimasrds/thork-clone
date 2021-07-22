package id.thork.app.pages.material_plan.element.detail_material_plan

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityMaterialPlanDetailBinding
import id.thork.app.pages.material_plan.MaterialPlanActivity
import timber.log.Timber

class MaterialPlanDetail : BaseActivity() {
    val TAG = MaterialPlanDetail::class.java.name

    val viewModel: MaterialPlanDetailViewModel by viewModels()
    private val binding: ActivityMaterialPlanDetailBinding by binding(R.layout.activity_material_plan_detail)
    var intentWorkorderId: String? = null

    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@MaterialPlanDetail
            vmdetail = viewModel
        }

        setupToolbarWithHomeNavigation(
            getString(R.string.wo_material_plan),
            navigation = false,
            filter = false,
            scannerIcon = false,
            notification = false,
            option = false,
            historyAttendanceIcon = false
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
        viewModel.wpmaterialCache.observe(this, Observer {
            binding.etMaterial.setText(it.itemNum)
            binding.includeMaterialPlanForm.etDescription.setText(it.description)
            binding.includeMaterialPlanForm.etQty.setText(it.itemqty.toString())
            binding.includeMaterialPlanForm.etStoreroom.setText(it.storeroom.toString())
        })

    }

    override fun setupListener() {
        super.setupListener()
        binding.btnSave.visibility = View.GONE

    }

    override fun goToPreviousActivity() {
        super.goToPreviousActivity()
        gotoMaterialPlan()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        gotoMaterialPlan()
    }

    private fun gotoMaterialPlan() {
        val intent = Intent(this, MaterialPlanActivity::class.java)
        intent.putExtra(BaseParam.WORKORDERID, intentWorkorderId?.toInt())
        startActivity(intent)
        finish()
    }
}