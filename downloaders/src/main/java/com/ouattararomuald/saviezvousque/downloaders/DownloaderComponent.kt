package com.ouattararomuald.saviezvousque.downloaders

import dagger.BindsInstance
import dagger.Component

@Component(modules = [
  DownloaderModule::class
])
interface DownloaderComponent {

  fun feedDownloader(): FeedDownloader

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun feedBaseUrl(feedBaseUrl: String): Builder

    fun build(): DownloaderComponent
  }

  companion object {
    fun builder(): Builder = DaggerDownloaderComponent.builder()
  }
}