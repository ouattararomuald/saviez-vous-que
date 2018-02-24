package com.ouattararomuald.saviezvousque.util

import android.app.Activity
import com.ouattararomuald.saviezvousque.App
import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent

internal fun Activity.getDbComponent(): DbComponent = getApp().dbComponent

internal fun Activity.getDownloaderComponent(): DownloaderComponent = getApp().downloaderComponent

internal fun Activity.getApp(): App = applicationContext as App