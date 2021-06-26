package cu.jaco.transito.repositories.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import cu.jaco.transito.BuildConfig

class AppPreferenceDataStore() : BaseEncryptedPreferenceDataStore() {

    override fun initPreferences(context: Context): SharedPreferences {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EncryptedSharedPreferences.create(
                context,
                BuildConfig.SHARED_PREF,
                MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            context.getSharedPreferences(
                BuildConfig.SHARED_PREF,
                Context.MODE_PRIVATE
            )
        }
    }

}