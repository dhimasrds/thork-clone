package id.thork.app.helper.builder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R
import id.thork.app.helper.builder.model.LocomotifAttribute
import id.thork.app.helper.builder.LocomotifHelper
import timber.log.Timber

class LocomotifAdapter(
    private val fieldName: String,
    private val dataset: MutableList<LocomotifAttribute>,
    internal var locomotifDialogItemClickListener: LocomotifDialogItemClickListener,
) : RecyclerView.Adapter<LocomotifAdapter.LocomotifViewHolder>(), Filterable {
    val TAG = LocomotifAdapter::class.java.name

    var datasetFilter: MutableList<LocomotifAttribute> = dataset

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): LocomotifViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.locomotif_dialog_item, parent, false)
        return LocomotifViewHolder(v)
    }

    override fun onBindViewHolder(locomotifViewHolder: LocomotifViewHolder, i: Int) {
        locomotifViewHolder.name.text = LocomotifHelper().NVL(datasetFilter[i].name, "-")
        locomotifViewHolder.value.text = LocomotifHelper().NVL(datasetFilter[i].value, "-")
    }

    override fun getItemCount(): Int {
        return datasetFilter.size
    }

    inner class LocomotifViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        val name = itemView.findViewById(R.id.name) as TextView
        val value = itemView.findViewById(R.id.value) as TextView

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Timber.tag(TAG).d("onClick() record: %s", datasetFilter[this.bindingAdapterPosition])
            locomotifDialogItemClickListener.clickOnItem(fieldName, datasetFilter[this.bindingAdapterPosition])
        }
    }

    interface LocomotifDialogItemClickListener {
        fun clickOnItem(fieldName: String, data: LocomotifAttribute)
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence.toString()
                Timber.tag(TAG).d("performFiltering() charString: %s", charString)
                if (charString.isEmpty()) {
                    datasetFilter = dataset
                } else {
                    val filterList: MutableList<LocomotifAttribute> = mutableListOf()
                    for (row in dataset) {
                        if (row.name.toString().toLowerCase().contains(charString)
                            || row.value.toString().toLowerCase().contains(charString)) {
                            filterList.add(row)
                        }
                    }
                    datasetFilter = filterList
                }
                val filterResults = FilterResults()
                filterResults.values = datasetFilter
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                datasetFilter = filterResults?.values as MutableList<LocomotifAttribute>
                Timber.tag(TAG).d("publishResults() datasetFilter: %s", datasetFilter)
                notifyDataSetChanged()
            }
        }
    }
}