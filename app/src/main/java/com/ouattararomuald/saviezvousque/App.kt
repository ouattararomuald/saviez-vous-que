package com.ouattararomuald.saviezvousque

import android.app.Application
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Stetho.initializeWithDefaults(this)
    }

    AndroidThreeTen.init(this)
  }
}
