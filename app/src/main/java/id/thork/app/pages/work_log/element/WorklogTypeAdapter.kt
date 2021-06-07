package id.thork.app.pages.work_log.element

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.databinding.CardviewListWorklogBinding
import id.thork.app.pages.find_asset_location.element.FindAssetAdapter
import id.thork.app.persistence.entity.WorklogEntity

/**
 * Created by Dhimas Saputra on 07/06/21
 * Jakarta, Indonesia.
 */
class WorklogTypeAdapter  constructor(
    private val worklogEntity: List<WorklogEntity>
) :
    RecyclerView.Adapter<WorklogTypeAdapter.ViewHolder>() {
    val TAG = FindAssetAdapter::class.java.name


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardviewListWorklogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(val binding: CardviewListWorklogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(worklogEntity: WorklogEntity) {
            binding.apply {
                cardWorklog.setOnClickListener {

                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val worklogEntity: WorklogEntity = worklogEntity[position]
        holder.bind(worklogEntity)
    }

    override fun getItemCount(): Int {
        return worklogEntity.size
    }


}
