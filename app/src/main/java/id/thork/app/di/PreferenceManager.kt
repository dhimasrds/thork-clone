package id.thork.app.di

import android.content.Context
import android.content.SharedPreferences
import id.thork.app.base.BaseParam
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(context: Context) {
    private val TAG: String = PreferenceManager::class.java.getName()

    var sharedPref: SharedPreferences = context.getSharedPreferences(BaseParam.APP_LOGIN_PREFERENCE, Context.MODE_PRIVATE)
    var sharedPrefEditor: SharedPreferences.Editor

    init {
        Timber.tag(TAG).i("PreferenceManager() preference manager: %s", sharedPref)
        sharedPrefEditor = sharedPref.edit()
        sharedPrefEditor.commit()
        Timber.tag(TAG).i("PreferenceManager() preference: %s shared editor: %s",
            sharedPref, sharedPrefEditor)
    }

    fun putString(key: String, value: String): SharedPreferences {
        Timber.tag(TAG).i("putString() value: %s", value)
        sharedPrefEditor.putString(key, value)
        sharedPrefEditor.commit()
        return sharedPref
    }

    fun putInt(key: String, value: Int):SharedPreferences {
        sharedPrefEditor.putInt(key, value)
        sharedPrefEditor.commit()
        return sharedPref
    }

    fun putBoolean(key: String, value: Boolean): SharedPreferences {
        sharedPrefEditor.putBoolean(key, value)
        sharedPrefEditor.commit()
        return sharedPref
    }

    fun getString(key: String): String {
        return sharedPref.getString(key,BaseParam.APP_EMPTY_STRING).toString()
    }

    fun getInt(key: String): Int {
        return sharedPref.getInt(key,0)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

}