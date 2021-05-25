package id.thork.app.pages.find_asset_location.element

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewFindassetBinding
import id.thork.app.pages.create_wo.CreateWoActivity
import id.thork.app.pages.find_asset_location.FindAssetActivity
import id.thork.app.persistence.entity.AssetEntity
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Dhimas Saputra on 25/05/21
 * Jakarta, Indonesia.
 */
class FindAssetAdapter constructor(
    private val assetEntity: List<AssetEntity>,
    private val requestOption: RequestOptions,
):
    RecyclerView.Adapter<FindAssetAdapter.ViewHolder>(),Filterable {
    var assetEntityFilterList = ArrayList<AssetEntity>()


    init {
        assetEntityFilterList = assetEntity as ArrayList<AssetEntity>
    }
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

                cardAsset.setOnClickListener {
                    val intent = Intent(BaseApplication.context, CreateWoActivity::class.java)
                    intent.putExtra(BaseParam.RFID_ASSET_IS_MATCH, assetIsMatch)
                    activity.setResult, intent)
                    finish()
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val assetEntity: AssetEntity = assetEntityFilterList[position]
        holder.bind(assetEntity)
    }

    override fun getItemCount(): Int {
        return assetEntityFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    assetEntityFilterList = assetEntity as ArrayList<AssetEntity>
                } else {
                    Timber.d("filter result :%s",assetEntity.size)
                    val resultList = ArrayList<AssetEntity>()
                    for (asset in assetEntity) {
                        if (asset.assetnum!!.toLowerCase().contains(charSearch.toLowerCase())) {
                            Timber.d("filter result text:%s",asset.assetnum)
                            resultList.add(asset)
                        }
                    }
                    assetEntityFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = assetEntityFilterList
                Timber.d("filter result :%s",filterResults)
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                assetEntityFilterList = results?.values as ArrayList<AssetEntity>
                notifyDataSetChanged()
            }

        }
    }

}