package id.thork.app.pages.main.element

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.databinding.FragmentBottomSheetToolTipBinding
import id.thork.app.network.response.work_order.Member
import id.thork.app.pages.create_wo.CreateWoActivity
import id.thork.app.pages.detail_wo.DetailWoActivity
import id.thork.app.utils.StringUtils
import timber.log.Timber
import java.sql.Time


@AndroidEntryPoint
class BottomSheetToolTipFragment : RoundedBottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetToolTipBinding
    lateinit var viewModel: MapViewModel
    val TAG = BottomSheetToolTipFragment::class.java.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentBottomSheetToolTipBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this


        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        val sharedViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        sharedViewModel.resultTagMarker.observe(viewLifecycleOwner, Observer {
            Timber.d("BottomSheetToolTipFragment() observer Tag Marker %s", it)
            validateViewBottomSheet(it)
        })

        sharedViewModel.woCache.observe(viewLifecycleOwner, Observer {
            Timber.d("BottomSheetToolTipFragment() observer etWoCache %s", it.wonum)
            val wonum: String = it.wonum!!
            binding.woTooltipContent.woTooltipWonum.text = it.wonum

            val moshi = Moshi.Builder().build()
            val memberJsonAdapter: JsonAdapter<Member> = moshi.adapter<Member>(
                Member::class.java
            )
            val currentMember = memberJsonAdapter.fromJson(it.syncBody)
            if (currentMember!!.location != null) {
                binding.woTooltipContent.woTooltipServiceAddress.text =
                    StringUtils.NVL(currentMember.location, BaseParam.APP_DASH)
            }

            if (currentMember.wopriority != null) {
                val priority = StringUtils.createPriority(currentMember.wopriority!!)
                binding.woTooltipContent.woTooltipPriority.text =
                    StringUtils.NVL(priority, BaseParam.APP_DASH)
            }

            binding.woTooltipContent.woTooltipButton.setOnClickListener {
                goToDetails(wonum)
            }
        })

        sharedViewModel.assetCache.observe(viewLifecycleOwner, Observer {
            Timber.d("BottomSheetToolTipFragment() observer etWoCache %s", it.assetnum)
            val assetnum : String = it.assetnum.toString()
            binding.assetTooltipContent.assetTooltipAssetName.text =
                    StringUtils.NVL(it.assetnum, BaseParam.APP_DASH)
                binding.assetTooltipContent.assetTooltipAssetDesc.text =
                    StringUtils.NVL(it.description, BaseParam.APP_DASH)
                binding.assetTooltipContent.assetTooltipServiceAddress.text =
                    StringUtils.NVL(it.formattedaddress, BaseParam.APP_DASH)
                binding.assetTooltipContent.assetTooltipDesc.text =
                    StringUtils.NVL(it.assetLocation, BaseParam.APP_DASH)

                binding.assetTooltipContent.assetTooltipButton.setOnClickListener {
                    goToCreateWoAsset(assetnum)
                }
        })

        sharedViewModel.locationCache.observe(viewLifecycleOwner, Observer {
            Timber.d("BottomSheetToolTipFragment() observer etWoCache %s", it.location)
            val location: String = it.location.toString()
            binding.locationTooltipContent.locationTooltipServiceAddress.text =
                StringUtils.NVL(it.formatAddress, BaseParam.APP_DASH)
            binding.locationTooltipContent.locationTooltipDesc.text =
                StringUtils.NVL(it.description, BaseParam.APP_DASH)

            binding.locationTooltipContent.locationTooltipButton.setOnClickListener {
                goToCreateWoLocation(location)            }
        })

        sharedViewModel.crewName.observe(viewLifecycleOwner, Observer {
            binding.crewTooltipContent.crewTooltipCrewName.text = it
        })
    }

    private fun validateViewBottomSheet(tag: String) {
        when (tag){
           BaseParam.APP_TAG_MARKER_ASSET -> {
                binding.assetTooltipContent.root.visibility = VISIBLE
            }
            BaseParam.APP_TAG_MARKER_WO -> {
                binding.woTooltipContent.root.visibility = VISIBLE
            }
            BaseParam.APP_TAG_MARKER_LOCATION -> {
                binding.locationTooltipContent.root.visibility = VISIBLE
            }
            BaseParam.APP_TAG_MARKER_CREW -> {
                binding.crewTooltipContent.root.visibility = VISIBLE
            }

        }
    }

    private fun goToDetails(wonum: String) {
        val intent = Intent(BaseApplication.context, DetailWoActivity::class.java)
        val bundle = Bundle()
        intent.putExtra(BaseParam.APP_WONUM, wonum)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(BaseApplication.context, intent, bundle)
    }

    private fun goToCreateWoLocation(location: String) {
        val intent = Intent(BaseApplication.context, CreateWoActivity::class.java)
        intent.putExtra(BaseParam.LOCATIONS, location)
        startActivity(intent)
    }

    private fun goToCreateWoAsset(asset: String) {
        val intent = Intent(BaseApplication.context, CreateWoActivity::class.java)
        intent.putExtra(BaseParam.ASSETNUM, asset)
        startActivity(intent)
    }
}