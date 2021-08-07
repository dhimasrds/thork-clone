package id.thork.app.pages.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import id.thork.app.R
import java.util.*

class FormMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_main)
    }

    fun addRecord(view: View) {
        val intent = Intent(this, FormActivity::class.java)
        intent.putExtra("PERSON", "EDIT")
        startActivity(intent)
    }
}