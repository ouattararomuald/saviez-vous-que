package com.ouattararomuald.saviezvousque.db

import android.content.Context
import dagger.BindsInstance
import dagger.Component

@Component(modules = [
  DbModule::class
])
interface DbComponent {

  fun feedRepository(): FeedRepository

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun appContext(name: Context): Builder

    @BindsInstance
    fun databaseName(name: String): Builder

    fun build(): DbComponent
  }

  companion object {
    fun builder(): Builder = DaggerDbComponent.builder()
  }
}