package id.thork.app

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.base.BaseActivity
import id.thork.app.databinding.ActivityMainBinding
import id.thork.app.pages.splash_screen.SplashScreenViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var mainViewModelFactory: MainViewModel.AssistedFactory

    val viewModel: MainViewModel by viewModels {
        MainViewModel.provideFactory(mainViewModelFactory, "REJA")
    }

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("OUTPUT1")

        binding.apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel
        }

        Log.d("output ya", "OK");
        Log.d("output ya", "${viewModel.memberInfo}")

    }

    fun onChange(view: View) {
        Log.d("onChange ya", "OK");
        Timber.d("onChange")
    }

}