package id.thork.app.pages.main.element

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.databinding.CardViewWorkOrderBinding
import id.thork.app.persistence.entity.WoEntity

/**
 * Created by Dhimas Saputra on 08/01/21
 * Jakarta, Indonesia.
 */
class WorkOrderAdapter(val list :List<WoEntity>) : RecyclerView.Adapter<WorkOrderAdapter.ViewHolder>() {
    class ViewHolder(val binding : CardViewWorkOrderBinding) :RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(woEntity: WoEntity){
            binding.tvWonum.text = woEntity.wonum
            binding.desc.text = woEntity.status
            binding.tvLocation.text = woEntity.laborCode
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardViewWorkOrderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = list[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}