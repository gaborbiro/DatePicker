package com.gb.datepicker

import com.gb.prefsutil.PrefsUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val prefsUtil: PrefsUtil) {

    val passwords: MutableMap<String, String>
        get() = prefsUtil.getMutable(PREF_PASSWORDS, mutableMapOf())

    var currentPasswordName: String?
        get() {
            val default: String? = null
            return prefsUtil.getOrNull(PREF_CURRENT_PASSWORD, default)
        }
        set(value) = if (value == null) prefsUtil.remove(PREF_CURRENT_PASSWORD) else prefsUtil.put(
            PREF_CURRENT_PASSWORD,
            value
        )

    fun clear() {
        prefsUtil.clear()
    }

    companion object {
        private const val PREF_PASSWORDS = "PREF_PASSWORDS"
        private const val PREF_CURRENT_PASSWORD = "PREF_CURRENT_PASSWORD"
    }
}
