package id.thork.app.pages.find_asset_location.element

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import id.thork.app.R
import id.thork.app.base.BaseApplication
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewFindassetBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.GlideApp
import id.thork.app.pages.create_wo.CreateWoActivity
import id.thork.app.pages.find_asset_location.FindAssetActivity
import id.thork.app.persistence.entity.AssetEntity
import id.thork.app.utils.PathUtils
import id.thork.app.utils.StringUtils
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 25/05/21
 * Jakarta, Indonesia.
 */
class FindAssetAdapter constructor(
    private val assetEntity: List<AssetEntity>,
    private val requestOption: RequestOptions,
    private  val activity: FindAssetActivity,
    private val preferenceManager: PreferenceManager

    ):
    RecyclerView.Adapter<FindAssetAdapter.ViewHolder>(),Filterable {
    var assetEntityFilterList = ArrayList<AssetEntity>()
    val TAG = FindAssetAdapter::class.java.name


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

    inner class ViewHolder(val binding: CardviewFindassetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(assetEntity: AssetEntity, activity: FindAssetActivity) {
            binding.apply {
                classifyImageThumbnail(assetEntity,ivAsset)
                assetnum.text =StringUtils.NVL(assetEntity.assetnum,BaseParam.APP_DASH)
                cardAsset.setOnClickListener {
                    val intent = Intent(BaseApplication.context, CreateWoActivity::class.java)
                    intent.putExtra(BaseParam.ASSETNUM, assetEntity.assetnum)
                    intent.putExtra(BaseParam.LOCATIONS, assetEntity.assetLocation)
                    activity.setResult(AppCompatActivity.RESULT_OK, intent)
                    activity.finish()
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val assetEntity: AssetEntity = assetEntityFilterList[position]
        holder.bind(assetEntity, activity)
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
                        if (asset.assetnum?.toLowerCase()?.contains(charSearch.toLowerCase()) == true) {
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

    private fun classifyImageThumbnail(
        assetEntity: AssetEntity,
        imageView: ImageView
    ) {
        assetEntity.whatIfNotNull { assetEntity ->
            var uri: Uri = PathUtils.getDrawableUri(context, R.drawable.default_image)
            assetEntity.image.whatIfNotNullOrEmpty {
                Timber.tag(TAG).d("classifyImageThumbnail() uristring: %s", it)

                if (it.startsWith("https")) {
                    val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
                    Timber.tag(TAG).d("classifyImageThumbnail() cookies: %s", cookie)
                    val glideUrl = GlideUrl(
                        it, LazyHeaders.Builder()
                            .addHeader("Cookie", cookie)
                            .build()
                    )
                    GlideApp.with(context).load(glideUrl)
                        .apply(requestOption)
                        .into(imageView)
                } else {
                        GlideApp.with(context).load(uri)
                            .apply(requestOption)
                            .into(imageView)
                    }
                }
            }
        }
    }

