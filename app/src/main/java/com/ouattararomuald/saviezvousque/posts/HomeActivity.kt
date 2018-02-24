package com.ouattararomuald.saviezvousque.posts

import android.databinding.ObservableList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent
import com.ouattararomuald.saviezvousque.util.getDbComponent
import com.ouattararomuald.saviezvousque.util.getDownloaderComponent
import com.ouattararomuald.saviezvousque.util.toDrawerItem
import javax.inject.Inject


class HomeActivity : AppCompatActivity() {

  @Inject
  lateinit var viewModel: HomeViewModel

  lateinit var dbComponent: DbComponent
  lateinit var downloaderComponent: DownloaderComponent

  private lateinit var drawer: Drawer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.home_activity)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)

    drawer = DrawerBuilder().withActivity(this)
        .withToolbar(toolbar)
        .withHasStableIds(true)
        .withSavedInstance(savedInstanceState)
        .build()

    dbComponent = getDbComponent()
    downloaderComponent = getDownloaderComponent()

    DaggerHomeActivityInjectorComponent.builder()
        .downloaderComponent(downloaderComponent)
        .activity(this)
        .build()
        .inject(this)

    viewModel.categories.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<Category>>() {
      override fun onChanged(sender: ObservableList<Category>?) {
        createDrawerItemsFromCategories(viewModel.categories)
      }

      override fun onItemRangeRemoved(sender: ObservableList<Category>?, positionStart: Int, itemCount: Int) {
        createDrawerItemsFromCategories(viewModel.categories)
      }

      override fun onItemRangeMoved(sender: ObservableList<Category>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
        createDrawerItemsFromCategories(viewModel.categories)
      }

      override fun onItemRangeInserted(sender: ObservableList<Category>?, positionStart: Int, itemCount: Int) {
        createDrawerItemsFromCategories(viewModel.categories)
      }

      override fun onItemRangeChanged(sender: ObservableList<Category>?, positionStart: Int, itemCount: Int) {
        createDrawerItemsFromCategories(viewModel.categories)
      }

    })
  }

  private fun createDrawerItemsFromCategories(categories: List<Category>) {
    if (categories.isNotEmpty()) {
      drawer.drawerItems.clear()
      categories.forEach {
        drawer.addItem(it.toDrawerItem())
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.start()
  }
}