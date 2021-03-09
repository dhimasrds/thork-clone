package id.thork.app.pages.list_material.element

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.persistence.entity.MaterialEntity

/**
 * Created by Raka Putra on 3/4/21
 * Jakarta, Indonesia.
 */
class ListMaterialAdapter(
    private val listMaterial: List<MaterialEntity?>?
) : RecyclerView.Adapter<ListMaterialAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvResultCode: TextView = itemView.findViewById(R.id.result_code)
        private var tvDateCode: TextView = itemView.findViewById(R.id.date_code)
        private var tvTimeCode: TextView = itemView.findViewById(R.id.time_code)

        fun bind(material: MaterialEntity) {
            tvResultCode.text = material.resultCode
            tvDateCode.text = material.date
            tvTimeCode.text = material.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_material, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listMaterial?.get(position)!!)
    }

    override fun getItemCount(): Int = listMaterial!!.size

}