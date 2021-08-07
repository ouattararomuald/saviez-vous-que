package com.ouattararomuald.saviezvousque

import com.ouattararomuald.saviezvousque.core.AppCoroutineDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoreModule {

  @Singleton
  @Provides
  fun provideDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
    io = Dispatchers.IO,
    main = Dispatchers.Main.immediate,
    computation = Dispatchers.Default,
  )
}