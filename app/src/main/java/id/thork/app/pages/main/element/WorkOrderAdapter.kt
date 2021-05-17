package id.thork.app.pages.main.element

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.StringUtil
import com.google.zxing.common.StringUtils
import id.thork.app.base.BaseParam
import id.thork.app.databinding.CardViewWorkOrderBinding
import id.thork.app.network.response.work_order.Member
import timber.log.Timber

/**
 * Created by Dhimas Saputra on 08/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderAdapter : PagingDataAdapter<Member, WorkOrderAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Member>(){

        override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem.wonum === newItem.wonum
        }

        override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem == newItem
        }
    }


    class ViewHolder(val binding: CardViewWorkOrderBinding) :RecyclerView.ViewHolder(binding.root) {

        fun bind(woEntity: Member){
            Timber.d("adapter wonum :%s",woEntity.wonum)
            Timber.d("adapter assetnum   :%s",woEntity.assetnum)
            binding.wo = woEntity
            binding.tvWonum.text = woEntity.wonum
            binding.desc.text = woEntity.description
            binding.tvWoAsset.text =id.thork.app.utils.StringUtils.NVL(woEntity.assetnum, BaseParam.APP_DASH)
            binding.tvWoLocation.text = woEntity.location
            binding.tvWoServiceAddress.text =id.thork.app.utils.StringUtils.truncate(woEntity.woserviceaddress!![0].formattedaddress, 13)
            binding.tvStatus.text = woEntity.status
            binding.executePendingBindings()
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