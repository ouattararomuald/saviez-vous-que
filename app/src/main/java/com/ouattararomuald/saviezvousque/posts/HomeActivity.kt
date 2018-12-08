package com.ouattararomuald.saviezvousque.posts

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.HomeActivityBinding
import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent
import com.ouattararomuald.saviezvousque.util.getDbComponent
import com.ouattararomuald.saviezvousque.util.getDownloaderComponent
import kotlinx.android.synthetic.main.home_app_bar.toolbar
import kotlinx.android.synthetic.main.home_app_bar.view.toolbar
import javax.inject.Inject

/** Displays the different categories and allow users to navigate between them. */
class HomeActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

  /** The [HomeViewModel] bound to this activity. */
  @Inject
  lateinit var viewModel: HomeViewModel

  private lateinit var sharedViewModel: SharedViewModel

  private lateinit var homeActivityBinding: HomeActivityBinding

  /** Dagger database component. */
  private lateinit var dbComponent: DbComponent

  /** Downloader component. */
  private lateinit var downloaderComponent: DownloaderComponent

  private lateinit var navigationView: NavigationView

  private var previousSelectedMenuItemId: Int = -1
  private var currentSelectedMenuItemId: Int = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    homeActivityBinding = DataBindingUtil.setContentView(this, R.layout.home_activity)

    setSupportActionBar(homeActivityBinding.drawerLayout.toolbar)

    sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

    val toggle = ActionBarDrawerToggle(
        this,
        homeActivityBinding.drawerLayout,
        homeActivityBinding.drawerLayout.toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close
    )
    homeActivityBinding.drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    navigationView = homeActivityBinding.navView
    navigationView.setNavigationItemSelectedListener(this)

    dbComponent = getDbComponent()
    downloaderComponent = getDownloaderComponent()

    DaggerHomeActivityInjectorComponent.builder()
        .databaseComponent(dbComponent)
        .downloaderComponent(downloaderComponent)
        .activity(this)
        .build()
        .inject(this)

    initializeBinding()

    val categoriesObserver = Observer<List<Category>> { displayCategories(it) }

    viewModel.categories.observe(this, categoriesObserver)

    val postsObserver = Observer<MutableMap<Int, List<Post>>> {
      updateSelectedPosts(currentSelectedMenuItemId)
    }

    viewModel.posts.observe(this, postsObserver)
  }

  private fun initializeBinding() {
    homeActivityBinding.viewModel = viewModel
  }

  override fun onSupportNavigateUp(): Boolean = NavigationUI.navigateUp(
      findNavController(R.id.nav_host_fragment),
      homeActivityBinding.drawerLayout
  )

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    if (item.itemId != previousSelectedMenuItemId) {
      previousSelectedMenuItemId = currentSelectedMenuItemId
    }

    currentSelectedMenuItemId = item.itemId

    if (item.itemId == R.id.archive_menu_item) {
      findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_archivesFragment)
    } else {
      if (previousSelectedMenuItemId == R.id.archive_menu_item
          && currentSelectedMenuItemId != R.id.archive_menu_item) {
        onBackPressed()
      }
      updateSelectedPosts(currentSelectedMenuItemId)
    }

    item.isChecked = true
    toolbar.title = item.title
    homeActivityBinding.drawerLayout.closeDrawers()
    return true
  }

  override fun onBackPressed() {
    if (isArchiveMenuSelected()) {
      previousSelectedMenuItemId = currentSelectedMenuItemId
      currentSelectedMenuItemId = sharedViewModel.categoryId.value ?: -1

      if (currentSelectedMenuItemId >= 0) {
        val menu = navigationView.menu

        menu.findItem(currentSelectedMenuItemId).isChecked = true
        toolbar.title = menu.findItem(currentSelectedMenuItemId).title
      }
    }
    super.onBackPressed()
  }

  private fun isArchiveMenuSelected(): Boolean = currentSelectedMenuItemId == R.id.archive_menu_item

  private fun updateSelectedPosts(categoryId: Int) {
    if (sharedViewModel.categoryId.value != categoryId && categoryId >= 0) {
      val posts = viewModel.getPostsByCategory(categoryId)
      if (posts.isNotEmpty()) {
        sharedViewModel.categoryId.value = categoryId
        sharedViewModel.posts.value = posts
      }
    }
  }

  private fun displayCategories(categories: List<Category>) {
    createDrawerItemsFromCategories(categories)
  }

  private fun createDrawerItemsFromCategories(categories: List<Category>) {
    if (categories.isNotEmpty()) {
      val menu = navigationView.menu
      categories.forEach {
        if (menu.findItem(it.id) == null) {
          menu.add(R.id.main_group, it.id, Menu.NONE, it.name)
        }
      }

      menu.setGroupCheckable(R.id.main_group, true, true)
      menu.getItem(1).isChecked = true
      currentSelectedMenuItemId = menu.getItem(1).itemId
    }
  }
}
