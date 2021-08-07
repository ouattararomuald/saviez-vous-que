package com.ouattararomuald.saviezvousque.db

import android.content.Context
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class SharedPreferenceManager @Inject constructor(@ActivityContext private val context: Context) {
  private val sharedPref = context.getSharedPreferences(
      context.getString(R.string.preference_name_key),
      Context.MODE_PRIVATE
  )

  var theme: String = "Deep Purple"
    get() = sharedPref.getString(context.getString(R.string.theme_name_key), "Deep Purple")!!
    set(value) {
      field = value
      sharedPref.edit().apply {
        putString(context.getString(R.string.theme_name_key), value)
        apply()
      }
    }
}