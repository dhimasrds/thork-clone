package id.thork.app.pages.detail_wo

import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import id.thork.app.R
import id.thork.app.base.BaseActivity
import id.thork.app.base.BaseParam
import id.thork.app.databinding.ActivityDetailWoBinding
import id.thork.app.pages.CustomDialogUtils
import id.thork.app.pages.detail_wo.element.DetailWoViewModel
import id.thork.app.utils.StringUtils

class DetailWoActivity : BaseActivity(), OnMapReadyCallback {
    private val TAG = DetailWoActivity::class.java.name

    private val detailWoViewModel: DetailWoViewModel by viewModels()
    private val binding: ActivityDetailWoBinding by binding(R.layout.activity_detail_wo)
    private lateinit var customDialogUtils: CustomDialogUtils
    private var intentWonum: String? = null
    private lateinit var toolBar: Toolbar
    private lateinit var map: GoogleMap


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@DetailWoActivity
            vm = detailWoViewModel
            transparentImage.setRootView(binding.mainScrollview)
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        customDialogUtils = CustomDialogUtils(this)
        setupToolbar()
        retrieveFromIntent()
    }

    override fun setupObserver() {
        super.setupObserver()
        detailWoViewModel.CurrentMember.observe(this, {
            val woPriority = StringUtils.NVL(it.wopriority, 0)
            binding.apply {
                wonum.text = it.wonum
                description.text = it.description
                status.text = it.status
                priority.text = StringUtils.createPriority(woPriority)
            }
        })
    }

    private fun setupToolbar() {
        toolBar = binding.toolbarDetails.wmsToolbar
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        binding.toolbarDetails.toolbarTitle.text = getString(R.string.wo_detail)
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun retrieveFromIntent() {
        intentWonum = intent.getStringExtra(BaseParam.APP_WONUM)
        detailWoViewModel.fetchWobyWonum(intentWonum!!)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            map = googleMap
        }
    }
}