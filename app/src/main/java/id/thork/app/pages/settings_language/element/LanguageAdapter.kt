package id.thork.app.pages.settings_language.element

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.persistence.entity.Language

/**
 * Created by Raka Putra on 2/19/21
 * Jakarta, Indonesia.
 */
class LanguageAdapter(
    private val recyclerViewItemClickListener: RecyclerViewItemClickListener,
    private val listLanguage: List<Language>
    ) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listLanguage[position])
    }

    override fun getItemCount(): Int = listLanguage.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var tvLanguage: TextView = itemView.findViewById(R.id.tv_settings_language)

        fun bind(lang: Language) {
            tvLanguage.text = lang.language
            itemView.setOnClickListener {
                recyclerViewItemClickListener.onItemClicked(lang)
            }
        }
    }

}