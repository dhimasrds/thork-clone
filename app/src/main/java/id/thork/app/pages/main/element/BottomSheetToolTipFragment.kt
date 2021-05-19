package id.thork.app.pages.main.element

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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

@AndroidEntryPoint
class BottomSheetToolTipFragment : RoundedBottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetToolTipBinding
    private val viewModel: MapViewModel by viewModels()
    val TAG = BottomSheetToolTipFragment::class.java.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentBottomSheetToolTipBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        retrieveFromIntent()
        setupObserver()
        return binding.root
    }

    private fun retrieveFromIntent() {
        val key = arguments?.getString("key")

        if (tag != null && tag.equals("asset") && key != null) {
            binding.assetTooltipContent.root.visibility = VISIBLE
            updateUiAssetBottomSheet(key)
        } else if (tag != null && tag.equals("wo") && key != null) {
            binding.woTooltipContent.root.visibility = VISIBLE
            updateUiWoBottomSheet(key)
        } else if (tag != null && tag.equals("location") && key != null) {
            binding.locationTooltipContent.root.visibility = VISIBLE
            updateUiLocationBottomSheet(key)
        }
    }

    private fun setupObserver() {
        viewModel.assetCache.observe(this, Observer {
            if (it != null) {
                binding.assetTooltipContent.assetTooltipAssetName.text =
                    StringUtils.NVL(it.assetnum, BaseParam.APP_DASH)
                binding.assetTooltipContent.assetTooltipAssetDesc.text =
                    StringUtils.NVL(it.description, BaseParam.APP_DASH)
                binding.assetTooltipContent.assetTooltipServiceAddress.text =
                    StringUtils.NVL(it.formattedaddress, BaseParam.APP_DASH)
                binding.assetTooltipContent.assetTooltipDesc.text =
                    StringUtils.NVL(it.assetLocation, BaseParam.APP_DASH)
                binding.assetTooltipContent.assetTooltipButton.setOnClickListener {
                    goToCreateWo()
                }
            }
        })

        viewModel.woCache.observe(this, Observer {
            if (it != null) {
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
            }
        })

        viewModel.locationCache.observe(this, Observer {
            if (it != null) {
                binding.locationTooltipContent.locationTooltipServiceAddress.text =
                    StringUtils.NVL(it.formatAddress, BaseParam.APP_DASH)
                binding.locationTooltipContent.locationTooltipDesc.text =
                    StringUtils.NVL(it.description, BaseParam.APP_DASH)

            }
        })
    }

    fun updateUiAssetBottomSheet(valueAsset: String) {
        viewModel.findAssetCache(valueAsset)
    }

    fun updateUiWoBottomSheet(wo: String) {
        viewModel.findWoCache(wo)
    }

    fun updateUiLocationBottomSheet(location: String) {
        viewModel.findLocationCache(location)
    }

    private fun goToDetails(wonum: String) {
        val intent = Intent(BaseApplication.context, DetailWoActivity::class.java)
        val bundle = Bundle()
        intent.putExtra(BaseParam.APP_WONUM, wonum)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextCompat.startActivity(BaseApplication.context, intent, bundle)
    }

    private fun goToCreateWo() {
        val intent = Intent(BaseApplication.context, CreateWoActivity::class.java)
        startActivity(intent)
    }

}