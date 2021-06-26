package cu.jaco.transito

import android.app.Application
import cu.jaco.transito.repositories.preferences.AppPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}