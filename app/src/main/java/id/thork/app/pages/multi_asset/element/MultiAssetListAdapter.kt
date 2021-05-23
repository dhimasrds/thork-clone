package id.thork.app.pages.multi_asset.element

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.pages.multi_asset.DetailsAssetActivity
import id.thork.app.persistence.entity.MultiAssetEntity
import timber.log.Timber

class MultiAssetListAdapter() :
    RecyclerView.Adapter<MultiAssetListAdapter.ViewHolder>() {

    var multiAssetEntity = mutableListOf<MultiAssetEntity>()

    fun setMultiAssetList(mutliAssetList: List<MultiAssetEntity>) {
        Timber.d("setMultiAssetList :%s", mutliAssetList.size)
        this.multiAssetEntity = mutliAssetList.toMutableList()
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(BaseApplication.context, intent, bundle)
        }
    }

    override fun getItemCount(): Int {
        return multiAssetEntity.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var assetnum: TextView = view.findViewById(R.id.assetnum)
        var location: TextView = view.findViewById(R.id.asset_location)
        var cardAsset: CardView = view.findViewById(R.id.card_asset)
    }
}