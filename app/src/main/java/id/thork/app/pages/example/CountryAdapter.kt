package id.thork.app.pages.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R

class CountryAdapter(private val countryList: ArrayList<Country>,
                     private val context: Context) :
    RecyclerView.Adapter<CountryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.form_list_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textViewName.text = countryList[position].name
        holder.card.setOnClickListener{
            if (context is CountryListActivity) {
                (context).pickCountry(countryList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName = itemView.findViewById(R.id.name) as TextView
        val card = itemView.findViewById(R.id.card) as CardView
    }
}