package id.thork.app.pages.list_material

import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityListMaterialBinding
import id.thork.app.pages.ScannerActivity
import id.thork.app.pages.list_material.element.ListMaterialAdapter
import id.thork.app.pages.list_material.element.ListMaterialViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Raka Putra on 3/2/21
 * Jakarta, Indonesia.
 */
class ListMaterialActivity : BaseActivity() {
    private val TAG = ListMaterialActivity::class.java.name

    private val viewModel: ListMaterialViewModel by viewModels()
    private val binding: ActivityListMaterialBinding by binding(R.layout.activity_list_material)

    private lateinit var adapter: ListMaterialAdapter
    private var intentWoId: Int? = null
    private var intentWoStatus: String? = null
    private var intentWonum: String? = null

    override fun setupView() {
        super.setupView()

        binding.apply {
            lifecycleOwner = this@ListMaterialActivity
            listMaterialViewModel = viewModel
        }
        setupToolbarWithHomeNavigation(
            getString(R.string.list_material),
            navigation = false,
            filter = false,
            scannerIcon = true,
            notification = false,
            option = false
        )
        retrieveFromIntent()
        if (intentWonum != null) {
            initAdapterByWonum()
        }
        initAdapter()
        initView()
    }

    private fun initView() {
        if (intentWonum!= null){
            checkingFileFromCreateWo()
        } else {
            checkingFileFromWoDetail()
        }
    }

    private fun checkingFileFromCreateWo(){
        val listMaterial = viewModel.fetchMaterialListByWonum(intentWonum!!)
        if (listMaterial != null && listMaterial.isNotEmpty()){
            initAdapterByWonum()
        } else {
            startQRScanner()
        }
    }

    private fun checkingFileFromWoDetail(){
        val listA = viewModel.fetchMaterialList(intentWoId!!)
        if (intentWoStatus != null && intentWoStatus.equals(BaseParam.COMPLETED) && listA?.isEmpty()!!) {
            Toast.makeText(this, R.string.stat_complete, Toast.LENGTH_SHORT).show()
        } else if (intentWoStatus != null && intentWoStatus.equals(BaseParam.COMPLETED) && listA != null && listA.isNotEmpty()) {
            initAdapter()
        } else if (listA != null && listA.isNotEmpty()) {
            initAdapter()
        } else startQRScanner()
    }

    private fun startQRScanner() {
        IntentIntegrator(this@ListMaterialActivity).setCaptureActivity(ScannerActivity::class.java).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode ,data)
        if (result != null) {
            if (result.contents != null && intentWonum != null) {
                saveMaterialFromCreateWo(result.contents, intentWonum!!)
            } else if (result.contents != null){
                saveMaterialFromWoDetail(result.contents, intentWoId!!)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun saveMaterialFromCreateWo(result: String?, wonum: String) {
        val date = SimpleDateFormat("yyyy MMM dd")
        val time = SimpleDateFormat("HH:mm")
        val currentDate = date.format(Date())
        val currentTime = time.format(Date())
        viewModel.saveListMaterialByWonum(currentDate, currentTime, result, wonum)
        binding.tvCodeText.text = result
        initAdapterByWonum()
    }

    fun saveMaterialFromWoDetail(result: String?, workorderid: Int) {
        val date = SimpleDateFormat("yyyy MMM dd")
        val time = SimpleDateFormat("HH:mm")
        val currentDate = date.format(Date())
        val currentTime = time.format(Date())
        viewModel.saveListMaterial(currentDate, currentTime, result, workorderid)
        binding.tvCodeText.text = result
        initAdapter()
    }

    private fun retrieveFromIntent() {
        intentWoId = intent.getIntExtra(BaseParam.WORKORDERID, 0)
        intentWoStatus = intent.getStringExtra(BaseParam.STATUS)
        intentWonum = intent.getStringExtra(BaseParam.WONUM)
    }

    private fun initAdapter() {
        val listMaterial = viewModel.fetchMaterialList(intentWoId!!)
        adapter = ListMaterialAdapter(listMaterial)
        binding.listMaterial.adapter = adapter
        binding.listMaterial.layoutManager = LinearLayoutManager(this)
    }

    private fun initAdapterByWonum() {
        val listMaterial = viewModel.fetchMaterialListByWonum(intentWonum!!)
        adapter = ListMaterialAdapter(listMaterial)
        binding.listMaterial.adapter = adapter
        binding.listMaterial.layoutManager = LinearLayoutManager(this)
    }

    override fun gotoScannerActivity() {
        startQRScanner()
    }

    override fun goToPreviousActivity() {
        finish()
    }
}