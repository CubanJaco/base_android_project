package cu.jaco.transito.repositories.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceDataStore

abstract class BaseEncryptedPreferenceDataStore : PreferenceDataStore() {

    abstract fun initPreferences(context: Context): SharedPreferences

    fun init(context: Context) {
        if (!::_preferences.isInitialized)
            _preferences = initPreferences(context)
    }

    private lateinit var _preferences: SharedPreferences
    var preferences: SharedPreferences
        get() {
            if (!::_preferences.isInitialized)
                throw IllegalStateException("Can't get preferences before initialize")
            return _preferences
        }
        set(value) {
            _preferences = value
        }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    override fun getBoolean(key: String, defValue: Boolean) =
        preferences.getBoolean(key, defValue)

    override fun putBoolean(key: String, value: Boolean) {
        preferences.edit {
            it.putBoolean(key, value)
        }
    }

    override fun getLong(key: String, defValue: Long) =
        preferences.getLong(key, defValue)

    override fun putLong(key: String, value: Long) {
        preferences.edit {
            it.putLong(key, value)
        }
    }

    override fun getInt(key: String, defValue: Int) =
        preferences.getInt(key, defValue)

    override fun putInt(key: String, value: Int) {
        preferences.edit {
            it.putInt(key, value)
        }
    }

    override fun getFloat(key: String?, defValue: Float) =
        preferences.getFloat(key, defValue)

    override fun putFloat(key: String?, value: Float) {
        preferences.edit {
            it.putFloat(key, value)
        }
    }

    override fun getString(key: String, defValue: String?) =
        preferences.getString(key, defValue)

    override fun putString(key: String, value: String?) {
        preferences.edit {
            it.putString(key, value)
        }
    }

    override fun getStringSet(
        key: String,
        defValues: MutableSet<String>?
    ): MutableSet<String>? = preferences.getStringSet(key, defValues)

    override fun putStringSet(key: String, values: MutableSet<String>?) {
        preferences.edit {
            it.putStringSet(key, values)
        }
    }

}