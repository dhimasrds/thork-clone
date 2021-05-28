package id.thork.app.pages.main.element

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.whatif.whatIfNotNull
import id.thork.app.base.BaseApplication.Constants.context
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardViewWorkOrderBinding
import id.thork.app.network.response.work_order.Member
import id.thork.app.pages.detail_wo.DetailWoActivity
import id.thork.app.persistence.dao.WoCacheDao
import id.thork.app.persistence.dao.WoCacheDaoImp
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 08/01/21
 * Jakarta, Indonesia.
 */

class WorkOrderAdapter : PagingDataAdapter<Member, WorkOrderAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Member>() {

        override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem.wonum === newItem.wonum
        }

        override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem == newItem
        }
    }


    class ViewHolder(val binding: CardViewWorkOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(woEntity: Member) {
            val woCacheDao: WoCacheDao = WoCacheDaoImp()
            val woCache =
                woCacheDao.findWoByWonumAndIslatest(woEntity.wonum.toString(), BaseParam.APP_TRUE)
            woCache.whatIfNotNull {
                if (it.syncStatus == BaseParam.APP_FALSE && it.isChanged == BaseParam.APP_TRUE) {
                    binding.ivOffline.visibility = View.VISIBLE
                } else {
                    binding.ivOffline.visibility = View.GONE
                }
            }

            Timber.d("adapter wonum :%s", woEntity.wonum)
            Timber.d("adapter assetnum   :%s", woEntity.assetnum)
            binding.wo = woEntity
            binding.tvWonum.text = woEntity.wonum
            binding.desc.text = woEntity.description
            binding.tvWoAsset.text =
                id.thork.app.utils.StringUtils.NVL(woEntity.assetnum, BaseParam.APP_DASH)
            binding.tvWoLocation.text = woEntity.location
            binding.tvWoServiceAddress.text = id.thork.app.utils.StringUtils.truncate(
                woEntity.woserviceaddress!![0].formattedaddress,
                13
            )
            binding.tvStatus.text = woEntity.status
            binding.executePendingBindings()

            woEntity.origrecordid.whatIfNotNull {
                binding.followupCardwo.visibility = View.VISIBLE
            }

            binding.cardWo.setOnClickListener {
                val intent = Intent(context, DetailWoActivity::class.java)
                val bundle = Bundle()
                intent.putExtra(BaseParam.APP_WONUM, woEntity.wonum)
                intent.putExtra(BaseParam.STATUS, woEntity.status)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(context, intent, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardViewWorkOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current!!)
    }

}