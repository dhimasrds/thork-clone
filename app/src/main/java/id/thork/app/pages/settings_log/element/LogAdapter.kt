package id.thork.app.pages.settings_log.element

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.persistence.entity.LogEntity

/**
 * Created by Raka Putra on 2/25/21
 * Jakarta, Indonesia.
 */
class LogAdapter(private val recyclerViewItemClickListener: LogRecyclerViewItemClickListener):
    PagingDataAdapter<LogEntity, LogAdapter.LogViewHolder>(Comparator){
    private val logList = mutableListOf<LogEntity>()

    companion object {
        object Comparator : DiffUtil.ItemCallback<LogEntity>() {
            override fun areItemsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: LogEntity, newItem: LogEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogAdapter.LogViewHolder {
        return LogViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.item_settings_logs, parent, false))    }

    override fun onBindViewHolder(holder: LogAdapter.LogViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int {
        return logList.size
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvMessage: TextView = itemView.findViewById(R.id.tv_settings_logs)

        fun bind(logEntity: LogEntity) {
            tvMessage.text = logEntity.message
            itemView.setOnClickListener {
                recyclerViewItemClickListener.onItemClicked(logEntity)
            }
        }
    }
}