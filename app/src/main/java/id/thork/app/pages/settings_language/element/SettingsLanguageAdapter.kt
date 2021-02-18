package id.thork.app.pages.settings_language.element

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.persistence.entity.LanguageEntity
import timber.log.Timber

/**
 * Created by Raka Putra on 2/15/21
 * Jakarta, Indonesia.
 */
class SettingsLanguageAdapter(
    private val recyclerViewItemClickListener: RecyclerViewItemClickListener,
) : PagingDataAdapter<LanguageEntity, SettingsLanguageAdapter.NoteViewHolder>(Comparator) {
    private val noteList = mutableListOf<LanguageEntity>()

    companion object {
        object Comparator : DiffUtil.ItemCallback<LanguageEntity>() {
            override fun areItemsTheSame(oldItem: LanguageEntity, newItem: LanguageEntity): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: LanguageEntity, newItem: LanguageEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        Timber.d("raka %s", noteList.size )
        return NoteViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_row, parent, false))
    }

    override fun getItemCount(): Int {
        Timber.d("raka %s", noteList.size )
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvLanguage: TextView = itemView.findViewById(R.id.tv_settings_language)

        fun bind(languageEntity: LanguageEntity) {
            tvLanguage.text = languageEntity.language
            itemView.setOnClickListener {
                recyclerViewItemClickListener.onItemClicked(languageEntity)
            }
        }
    }
}