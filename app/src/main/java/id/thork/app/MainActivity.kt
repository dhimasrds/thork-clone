package id.thork.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityMainBinding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    val TAG = MainActivity::class.java.name

    val viewModel: MainViewModel by viewModels()

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        changeDefaultQuotes()
    }


    override fun setupView() {
        super.setupView()
        binding.apply {
            lifecycleOwner = this@MainActivity
            reza = viewModel
        }
    }

    override fun setupListener() {
        super.setupListener()
    }

    override fun setupObserver() {
        super.setupObserver()
        
        viewModel.quote.observe(this, Observer { quot ->
            Timber.tag(TAG).i("setupObserver() quote value: $quot")
            if (!quot.isNullOrEmpty()) {
                saveAndToast(quot)
            }
        })
    }

    private fun saveAndToast(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
        //SAVE IN HERE  ///
        ////
        /////
    }

    private fun changeDefaultQuotes() {
        binding.quotes.text = "Never give up"
    }

    fun onChange(view: View) {
        viewModel.changeRandomQuotes()
    }
}