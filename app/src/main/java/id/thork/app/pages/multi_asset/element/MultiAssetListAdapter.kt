package id.thork.app.pages.multi_asset.element

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R

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
    }

    override fun getItemCount(): Int {
        return multiAssetEntity.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var assetnum: TextView = view.findViewById(R.id.assetnum)
        var location: TextView = view.findViewById(R.id.asset_location)
    }
}