package com.ouattararomuald.saviezvousque.posts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.HomeActivityBinding
import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.db.SharedPreferenceManager
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent
import com.ouattararomuald.saviezvousque.posts.archives.PaginatedPostView
import com.ouattararomuald.saviezvousque.posts.theme.ThemeDialogPicker
import com.ouattararomuald.saviezvousque.posts.theme.ThemeStyleFactory
import com.ouattararomuald.saviezvousque.posts.views.PostView
import com.ouattararomuald.saviezvousque.util.getDbComponent
import com.ouattararomuald.saviezvousque.util.getDownloaderComponent
import kotlinx.android.synthetic.main.home_app_bar.toolbar
import kotlinx.android.synthetic.main.home_app_bar.view.toolbar
import kotlinx.android.synthetic.main.home_content.view.content_home
import kotlinx.android.synthetic.main.home_content.view.paginated_post_view
import kotlinx.android.synthetic.main.home_content.view.posts_view
import javax.inject.Inject

/** Displays the different categories and allow users to navigate between them. */
class HomeActivity : AppCompatActivity() {

  /** The [HomeViewModel] bound to this activity. */
  @Inject lateinit var viewModel: HomeViewModel

  private lateinit var homeActivityBinding: HomeActivityBinding

  /** Dagger database component. */
  private lateinit var dbComponent: DbComponent

  /** Dagger downloader component. */
  private lateinit var downloaderComponent: DownloaderComponent

  private lateinit var navigationView: NavigationView

  private lateinit var archivePostView: PaginatedPostView
  private lateinit var postsView: PostView

  private var currentSelectedMenuItem: MenuItem? = null

  private val intentFilter: IntentFilter = IntentFilter().apply {
    @Suppress("DEPRECATION")
    addAction(ConnectivityManager.CONNECTIVITY_ACTION)
  }
  private val connectivityStatusMonitor = ConnectivityStatusMonitor()

  private lateinit var themeDialogPicker: ThemeDialogPicker

  private lateinit var sharedPreferenceManager: SharedPreferenceManager

  override fun onCreate(savedInstanceState: Bundle?) {
    sharedPreferenceManager = SharedPreferenceManager(this)
    setTheme(ThemeStyleFactory.getStyle(this, sharedPreferenceManager.theme))
    super.onCreate(savedInstanceState)
    homeActivityBinding = DataBindingUtil.setContentView(this, R.layout.home_activity)

    setSupportActionBar(homeActivityBinding.drawerLayout.toolbar)

    themeDialogPicker = ThemeDialogPicker(this) { themeStyle, themeName ->
      sharedPreferenceManager.theme = themeName
      setTheme(themeStyle)
      recreate()
    }

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
    archivePostView = homeActivityBinding.drawerLayout.content_home.paginated_post_view
    postsView = homeActivityBinding.drawerLayout.content_home.posts_view

    dbComponent = getDbComponent()
    downloaderComponent = getDownloaderComponent()

    DaggerHomeActivityInjectorComponent.builder()
        .databaseComponent(dbComponent)
        .downloaderComponent(downloaderComponent)
        .activity(this)
        .build()
        .inject(this)

    homeActivityBinding.viewModel = viewModel

    observeCategories()

    observePosts()

    configureNavigationMenuEventClickListener()
  }

  private fun observeCategories() {
    val categoriesObserver = Observer<List<Category>> { displayCategories(it) }
    viewModel.categories.observe(this, categoriesObserver)
  }

  private fun observePosts() {
    val postsObserver = Observer<Map<Int, List<Post>>> {
      currentSelectedMenuItem?.let { updateSelectedPosts(it.itemId) }
    }
    viewModel.posts.observe(this, postsObserver)
  }

  private fun configureNavigationMenuEventClickListener() {
    navigationView.setNavigationItemSelectedListener { menuItem ->
      currentSelectedMenuItem = menuItem

      menuItem.isChecked = true
      toolbar.title = menuItem.title

      archivePostView.isVisible = menuItem.itemId == R.id.archive_menu_item
      postsView.isVisible = !archivePostView.isVisible

      when (menuItem.itemId) {
        R.id.archive_menu_item -> archivePostView.configureDataSourceFactory(
            getDownloaderComponent().feedDownloader(),
            getDbComponent().feedRepository(),
            this
        )
        R.id.choose_theme_menu_item -> themeDialogPicker.show()
        else -> currentSelectedMenuItem?.let { updateSelectedPosts(it.itemId) }
      }

      homeActivityBinding.drawerLayout.closeDrawers()

      return@setNavigationItemSelectedListener true
    }
  }

  override fun onResume() {
    super.onResume()
    registerReceiver(connectivityStatusMonitor, intentFilter)
  }

  override fun onPause() {
    super.onPause()
    unregisterReceiver(connectivityStatusMonitor)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.home, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle item selection
    return when (item.itemId) {
      R.id.refresh_menu_item -> {
        viewModel.refreshData()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun updateSelectedPosts(categoryId: Int) {
    val posts = viewModel.getPostsByCategory(categoryId)
    if (posts.isNotEmpty()) {
      postsView.displayPosts(categoryId, posts)
    }
  }

  private fun displayCategories(categories: List<Category>) {
    createDrawerItemsFromCategories(categories)
  }

  private fun createDrawerItemsFromCategories(categories: List<Category>) {
    if (categories.isNotEmpty()) {
      val menu = navigationView.menu
      categories.forEach { category ->
        if (menu.findItem(category.id) == null) {
          menu.add(R.id.main_group, category.id, Menu.NONE, category.name)
        }
      }

      menu.setGroupCheckable(R.id.main_group, true, true)
      menu.getItem(2).isChecked = true
      currentSelectedMenuItem = menu.getItem(2)
    }
  }

  inner class ConnectivityStatusMonitor : BroadcastReceiver() {

    @Suppress("DEPRECATION") private fun isInternetAvailable(): Boolean {
      val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

      val activeNetwork = cm.activeNetworkInfo
      return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
      val actionOfIntent = intent.action
      val isConnected = isInternetAvailable()
      if (actionOfIntent == ConnectivityManager.CONNECTIVITY_ACTION) {
        if (isConnected) {
          archivePostView.notifyInternetAvailable()
        } else {
          archivePostView.notifyInternetLost()
        }
      }
    }
  }
}
