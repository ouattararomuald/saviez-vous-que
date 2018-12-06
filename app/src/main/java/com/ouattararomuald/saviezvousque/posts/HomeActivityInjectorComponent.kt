package com.ouattararomuald.saviezvousque.posts

import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [DbComponent::class, DownloaderComponent::class],
    modules = [HomeModule::class]
)
interface HomeActivityInjectorComponent {

  fun inject(homeActivity: HomeActivity)

  @Component.Builder
  interface Builder {

    fun downloaderComponent(downloaderComponent: DownloaderComponent): Builder

    fun databaseComponent(databaseComponent: DbComponent): Builder

    @BindsInstance
    fun activity(activity: HomeActivity): Builder

    fun build(): HomeActivityInjectorComponent
  }

  companion object {
    fun builder(): Builder = HomeActivityInjectorComponent.builder()
  }
}
