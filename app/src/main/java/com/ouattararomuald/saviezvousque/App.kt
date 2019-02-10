package com.ouattararomuald.saviezvousque

import android.app.Application
import com.facebook.stetho.Stetho
import com.ouattararomuald.saviezvousque.db.DaggerDbComponent
import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.downloaders.DaggerDownloaderComponent
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent

class App : Application() {

  /** Dagger Database Component. */
  lateinit var dbComponent: DbComponent
    private set

  /** Dagger Downloader Component. */
  lateinit var downloaderComponent: DownloaderComponent
    private set

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Stetho.initializeWithDefaults(this)
    }

    dbComponent = DaggerDbComponent.builder()
        .appContext(this)
        .databaseName(BuildConfig.DATABASE_NAME)
        .build()

    downloaderComponent = DaggerDownloaderComponent.builder()
        .feedBaseUrl(BuildConfig.FEED_BASE_URL)
        .build()
  }

}
