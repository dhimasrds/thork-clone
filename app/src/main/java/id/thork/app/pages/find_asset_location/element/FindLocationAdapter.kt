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
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardviewFindassetBinding
import id.thork.app.di.module.PreferenceManager
import id.thork.app.network.GlideApp
import id.thork.app.pages.create_wo.CreateWoActivity
import id.thork.app.pages.find_asset_location.FindLocationActivity
import id.thork.app.persistence.entity.LocationEntity
import id.thork.app.utils.PathUtils
import id.thork.app.utils.StringUtils
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 27/05/21
 * Jakarta, Indonesia.
 */
class FindLocationAdapter  constructor(
    private val locationEntity: List<LocationEntity>,
    private val requestOption: RequestOptions,
    private  val activity: FindLocationActivity,
    private val preferenceManager: PreferenceManager

):
    RecyclerView.Adapter<FindLocationAdapter.ViewHolder>(), Filterable {
    var locationEntityFilterList = ArrayList<LocationEntity>()
    val TAG = FindLocationAdapter::class.java.name


    init {
        locationEntityFilterList = locationEntity as ArrayList<LocationEntity>

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
        fun bind(locationEntity : LocationEntity, activity: FindLocationActivity) {
            with(binding) {
                classifyImageThumbnail(locationEntity,ivAsset)
                assetnum.text = StringUtils.NVL(locationEntity.location,BaseParam.APP_DASH)
                cardAsset.setOnClickListener {
                    val intent = Intent(BaseApplication.context, CreateWoActivity::class.java)
                    intent.putExtra(BaseParam.LOCATIONS, locationEntity.location)
                    activity.setResult(AppCompatActivity.RESULT_OK, intent)
                    activity.finish()
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locationEntity: LocationEntity = locationEntityFilterList[position]
        holder.bind(locationEntity, activity)
    }

    override fun getItemCount(): Int {
        return locationEntityFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    locationEntityFilterList = locationEntity as ArrayList<LocationEntity>
                } else {
                    Timber.d("filter result :%s",locationEntity.size)
                    val resultList = ArrayList<LocationEntity>()
                    for (location in locationEntity) {
                        if (location.formatAddress?.toLowerCase()?.contains(charSearch.toLowerCase()) == true) {
                            Timber.d("filter result text:%s",location.formatAddress)
                            resultList.add(location)
                        }
                    }
                    locationEntityFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = locationEntityFilterList
                Timber.d("filter result :%s",filterResults)
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                locationEntityFilterList = results?.values as ArrayList<LocationEntity>
                notifyDataSetChanged()
            }

        }
    }

    private fun classifyImageThumbnail(
        locationEntity: LocationEntity,
        imageView: ImageView
    ) {
        locationEntity.whatIfNotNull { locationEntity ->
            var uri: Uri = PathUtils.getDrawableUri(BaseApplication.context, R.drawable.default_image)
            locationEntity.image.whatIfNotNullOrEmpty {
                Timber.tag(TAG).d("classifyImageThumbnail() uristring: %s", it)

                if (it.startsWith("https")) {
                    val cookie: String = preferenceManager.getString(BaseParam.APP_MX_COOKIE)
                    Timber.tag(TAG).d("classifyImageThumbnail() cookies: %s", cookie)
                    val glideUrl = GlideUrl(
                        it, LazyHeaders.Builder()
                            .addHeader("Cookie", cookie)
                            .build()
                    )
                    GlideApp.with(BaseApplication.context).load(glideUrl)
                        .apply(requestOption)
                        .into(imageView)
                } else {
                    GlideApp.with(BaseApplication.context).load(uri)
                        .apply(requestOption)
                        .into(imageView)
                }
            }
        }
    }
}