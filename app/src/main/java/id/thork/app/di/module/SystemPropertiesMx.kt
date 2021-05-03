package id.thork.app.di.module

import android.content.Context
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

/**
 * Created by M.Reza Sulaiman on 29/04/21
 * Jakarta, Indonesia.
 */
@Module
@InstallIn(SingletonComponent::class)
class SystemPropertiesMx @Inject constructor(context: Context) {
    val TAG = SystemPropertiesMx::class.java.name
    //TODO hardcode param properties api key for query

    val FSM_APP_GOOGLE_MAP_WHATEVER = "fsm.googlemaps.api.key"



}