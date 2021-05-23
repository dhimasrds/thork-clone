package id.thork.app.pages.multi_asset.element

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.finishAfterTransition
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import id.thork.app.R
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.pages.ScannerActivity
import id.thork.app.pages.multi_asset.DetailsAssetActivity
import id.thork.app.pages.multi_asset.ListAssetActivity
import id.thork.app.pages.rfid_mutli_asset.RfidMultiAssetActivity
import id.thork.app.persistence.entity.MultiAssetEntity
import timber.log.Timber

class MultiAssetListAdapter() :
    RecyclerView.Adapter<MultiAssetListAdapter.ViewHolder>() {

    var multiAssetEntity = mutableListOf<MultiAssetEntity>()
    private lateinit var activity: ListAssetActivity

    fun setMultiAssetList(mutliAssetList: List<MultiAssetEntity>, activity: ListAssetActivity) {
        Timber.d("setMultiAssetList :%s", mutliAssetList.size)
        this.multiAssetEntity = mutliAssetList.toMutableList()
        this.activity = activity
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultiAssetListAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_list_asset, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val multiAssetEntity = multiAssetEntity[position]
        holder.assetnum.text = multiAssetEntity.assetNum
        holder.location.text = multiAssetEntity.location
        holder.cardAsset.setOnClickListener {
            val intent = Intent(BaseApplication.context, DetailsAssetActivity::class.java)
            val bundle = Bundle()
            intent.putExtra(BaseParam.ASSETNUM, multiAssetEntity.assetNum)
            intent.putExtra(BaseParam.WONUM, multiAssetEntity.wonum)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(BaseApplication.context, intent, bundle)
            finishAfterTransition(activity)
//            startActivityForResult(activity, intent, BaseParam.REQUEST_CODE_MULTI_ASSET, bundle)
        }

        if (multiAssetEntity.isScan?.equals(BaseParam.APP_TRUE) == true) {
            holder.ivChecklist.visibility = View.GONE
            holder.ivChecklistDone.visibility = View.VISIBLE
        }

        holder.btnRfid.setOnClickListener {
            val intent = Intent(BaseApplication.context, RfidMultiAssetActivity::class.java)
            val bundle = Bundle()
            intent.putExtra(BaseParam.RFID_ASSETNUM, multiAssetEntity.assetNum)
            intent.putExtra(BaseParam.WONUM, multiAssetEntity.wonum)
            startActivityForResult(activity, intent, BaseParam.RFID_REQUEST_CODE_MULTIASSET, bundle)
        }

        holder.btnQr.setOnClickListener {
            IntentIntegrator(activity).apply {
                setCaptureActivity(ScannerActivity::class.java)
                setRequestCode(BaseParam.BARCODE_REQUEST_CODE_MULTIASSET)
                initiateScan()
            }
        }
    }

    override fun getItemCount(): Int {
        return multiAssetEntity.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var assetnum: TextView = view.findViewById(R.id.assetnum)
        var location: TextView = view.findViewById(R.id.asset_location)
        var cardAsset: CardView = view.findViewById(R.id.card_asset)
        var btnRfid: Button = view.findViewById(R.id.button_rfid)
        var btnQr: Button = view.findViewById(R.id.button_qr)
        var ivChecklist: ImageView = view.findViewById(R.id.iv_checklist)
        var ivChecklistDone: ImageView = view.findViewById(R.id.iv_checklist_done)
    }

}