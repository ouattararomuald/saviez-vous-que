package com.ouattararomuald.saviezvousque.db

import android.content.Context
import com.ouattararomuald.saviezvousque.db.daos.CategoryDao
import com.ouattararomuald.saviezvousque.db.daos.PostDao
import dagger.BindsInstance
import dagger.Component

@Component(modules = [
  DbModule::class
])
interface DbComponent {

  fun feedRepository(): FeedRepository

  fun categoryDao(): CategoryDao

  fun postDao(): PostDao

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