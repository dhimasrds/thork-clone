package id.thork.app.pages.main.element

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.databinding.CardViewWorkOrderBinding
import id.thork.app.network.response.work_order.Member

/**
 * Created by Dhimas Saputra on 08/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderAdapter : PagingDataAdapter<Member,WorkOrderAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Member>(){

        override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem.wonum === newItem.wonum
        }

        override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem == newItem
        }
    }


    class ViewHolder(val binding : CardViewWorkOrderBinding) :RecyclerView.ViewHolder(binding.root) {

        fun bind(woEntity: Member){
            binding.wo = woEntity
            binding.tvWonum.text = woEntity.wonum
            binding.desc.text = woEntity.description
            binding.tvLocation.text = woEntity.location
            binding.tvStatus.text = woEntity.status
            binding.executePendingBindings()
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardViewWorkOrderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current!!)
    }

}