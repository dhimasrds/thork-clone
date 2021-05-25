package id.thork.app.pages.find_asset_location.element

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import id.thork.app.BuildConfig
import id.thork.app.databinding.AttachmentItemBinding
import id.thork.app.databinding.CardViewWorkOrderBinding
import id.thork.app.databinding.CardviewFindassetBinding
import id.thork.app.pages.main.element.WorkOrderAdapter
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.persistence.entity.AttachmentEntity
import id.thork.app.utils.StringUtils
import java.io.File

/**
 * Created by Dhimas Saputra on 25/05/21
 * Jakarta, Indonesia.
 */
class FindAssetAdapter(
    private val assetEntity: List<AssetEntity>,
    private val requestOption: RequestOptions
) :
    RecyclerView.Adapter<FindAssetAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardviewFindassetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class ViewHolder(val binding: CardviewFindassetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(assetEntity: AssetEntity) {
            binding.apply {
                assetnum.text = assetEntity.assetnum
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val assetEntity: AssetEntity = assetEntity[position]
        holder.bind(assetEntity)
    }

    override fun getItemCount(): Int {
        return assetEntity.size
    }
}