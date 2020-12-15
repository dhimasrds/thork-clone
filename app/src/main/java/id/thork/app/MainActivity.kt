package id.thork.app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityMainBinding
import id.thork.app.di.ApiKey
import id.thork.app.di.LibraryKey
import id.thork.app.di.PreferenceManager
import id.thork.app.extensions.argument
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @ApiKey
    @Inject
    lateinit var baseUrl: String

    @LibraryKey
    @Inject
    lateinit var baseUrl2: String

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var preferencesManager: PreferenceManager

//    @Inject
//    lateinit var mainViewModelFactory: MainViewModel.AssistedFactory

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)

    @Inject
    lateinit var mainViewModelFactory : MainViewModel.AssistedFactory

    val viewModel: MainViewModel by viewModels {
        MainViewModel.provideFactory(mainViewModelFactory, "REJA")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Here", "baseurl " + baseUrl)
        Log.d("Here", "baseurl2 " + baseUrl2)
        Log.d("Context", "$context")
        Log.d("PreferenceManager", "$preferencesManager")

        preferencesManager.putString("H1", "PERTAMA")
        Log.d("PreferenceManager H1", "${preferencesManager.getString("H1")}")

        binding.apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel
        }
    }

}