package id.thork.app.pages.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.thork.app.R

class CountryListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_list)

        val linearLayout = findViewById(R.id.linearLayout) as LinearLayout
        val recyclerView =  RecyclerView(this)
        val countries = ArrayList<Country>()
        var p = Country();
        p.name = "Indonesia"
        p.id = "ID"
        countries.add(p)

        p = Country();
        p.name = "Malaysia"
        p.id = "MY"
        countries.add(p)

        p = Country();
        p.name = "Singapore"
        p.id = "SG"
        countries.add(p)

        val adapter = CountryAdapter(countries, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        linearLayout.addView(recyclerView)
    }

    fun pickCountry(country: Country) {
        Log.d("pickCountry", "pickCountry " + country)
        val intent = Intent()
        intent.putExtra("ID", country.id)
        intent.putExtra("NAME", country.name)
        setResult(RESULT_OK, intent)
        finish()
    }
}