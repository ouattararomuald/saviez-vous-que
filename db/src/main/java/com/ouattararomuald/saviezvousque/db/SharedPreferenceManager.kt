package com.ouattararomuald.saviezvousque.db

import android.annotation.SuppressLint
import android.content.Context

class SharedPreferenceManager(private val context: Context) {
  private val sharedPref = context.getSharedPreferences(
      context.getString(R.string.preference_name_key),
      Context.MODE_PRIVATE
  )

  var theme: String = "Deep Purple"
    get() = sharedPref.getString(context.getString(R.string.theme_name_key), "Deep Purple")!!
    @SuppressLint("ApplySharedPref")
    set(value) {
      field = value
      sharedPref.edit().apply {
        putString(context.getString(R.string.theme_name_key), value)
        commit()
      }
    }
}