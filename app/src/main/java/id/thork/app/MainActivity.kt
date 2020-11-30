package id.thork.app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import id.thork.app.di.ApiKey
import id.thork.app.di.LibraryKey
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @ApiKey
    @Inject
    lateinit var baseUrl:String

    @LibraryKey
    @Inject
    lateinit var baseUrl2:String

    @Inject
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Here","baseurl " + baseUrl)
        Log.d("Here","baseurl2 " + baseUrl2)
        Log.d("Context", "$context")
    }
}