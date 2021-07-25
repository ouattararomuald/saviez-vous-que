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
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.databinding.HomeActivityBinding
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.ouattararomuald.saviezvousque.db.SharedPreferenceManager
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent
import com.ouattararomuald.saviezvousque.posts.archives.PaginatedPostView
import com.ouattararomuald.saviezvousque.posts.theme.ThemeDialogPicker
import com.ouattararomuald.saviezvousque.posts.theme.ThemeStyleFactory
import com.ouattararomuald.saviezvousque.posts.views.PostListView
import com.ouattararomuald.saviezvousque.util.getDbComponent
import com.ouattararomuald.saviezvousque.util.getDownloaderComponent
import javax.inject.Inject

/** Displays the different categories and allow users to navigate between them. */
class HomeActivity : AppCompatActivity(), HomeContract.View {

  /** The [HomePresenter] bound to this activity. */
  @Inject lateinit var homePresenter: HomePresenter

  private lateinit var homeActivityBinding: HomeActivityBinding

  /** Dagger database component. */
  private lateinit var dbComponent: DbComponent

  /** Dagger downloader component. */
  private lateinit var downloaderComponent: DownloaderComponent

  private lateinit var navigationView: NavigationView

  /** View used to display list of [Post]s in a paginated view. */
  private lateinit var archivePostView: PaginatedPostView

  /** View used to display list of [Post]s for one category at a time. */
  private lateinit var postListView: PostListView

  private var currentSelectedMenuItem: MenuItem? = null
  private var refreshMenuItem: MenuItem? = null

  private val intentFilter: IntentFilter = IntentFilter().apply {
    @Suppress("DEPRECATION")
    addAction(ConnectivityManager.CONNECTIVITY_ACTION)
  }
  private val connectivityStatusMonitor = ConnectivityStatusMonitor()

  private lateinit var themeDialogPicker: ThemeDialogPicker

  private lateinit var sharedPreferenceManager: SharedPreferenceManager

  private val menuManager: MenuManager by lazy {
    val menu = navigationView.menu
    MenuManager(menu, DEFAULT_SELECTED_MENU_ITEM_INDEX)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    sharedPreferenceManager = SharedPreferenceManager(this)
    setTheme(ThemeStyleFactory.getStyle(this, sharedPreferenceManager.theme))
    super.onCreate(savedInstanceState)

    homeActivityBinding = HomeActivityBinding.inflate(layoutInflater)
    setContentView(homeActivityBinding.root)

    setSupportActionBar(homeActivityBinding.appBarContainer.toolbar)

    themeDialogPicker = ThemeDialogPicker(this) { themeStyle, themeName ->
      sharedPreferenceManager.theme = themeName
      setTheme(themeStyle)
      recreate()
    }

    val toggle = ActionBarDrawerToggle(
        this,
        homeActivityBinding.drawerLayout,
        homeActivityBinding.appBarContainer.toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close
    )
    homeActivityBinding.drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    navigationView = homeActivityBinding.navView
    archivePostView = homeActivityBinding.appBarContainer.homeContentContainer.paginatedPostView
    postListView = homeActivityBinding.appBarContainer.homeContentContainer.postsView

    dbComponent = getDbComponent()
    downloaderComponent = getDownloaderComponent()

    DaggerHomeActivityInjectorComponent.builder()
        .databaseComponent(dbComponent)
        .downloaderComponent(downloaderComponent)
        .activity(this)
        .view(this)
        .build()
        .inject(this)

    //homeActivityBinding.viewModel = homePresenter

    observeCategories()
    observePosts()

    configureNavigationMenuEventClickListener()
  }

  private fun observeCategories() {
    val categoriesObserver = Observer<List<CategoryIdAndName>> { displayCategories(it) }
    homePresenter.categoriesObservable.observe(this, categoriesObserver)
  }

  private fun observePosts() {
    val postsObserver = Observer<List<PostWithCategory>> {
      currentSelectedMenuItem?.let { displayPostsOfCategory(it.itemId) }
    }
    homePresenter.postsObservable.observe(this, postsObserver)
  }

  private fun configureNavigationMenuEventClickListener() {
    navigationView.setNavigationItemSelectedListener { menuItem ->
      currentSelectedMenuItem = menuItem

      menuItem.isChecked = true

      if (menuItem.itemId != R.id.choose_theme_menu_item) {
        homeActivityBinding.appBarContainer.toolbar.title = menuItem.title
      }

      chooseViewToDisplay(menuItem)

      when (menuItem.itemId) {
        R.id.archive_menu_item -> {
          refreshMenuItem?.isVisible = false
          archivePostView.configureDataSourceFactory(
              getDownloaderComponent().feedDownloader(),
              getDbComponent().feedRepository(),
              this
          )
        }
        R.id.choose_theme_menu_item -> themeDialogPicker.show()
        else -> {
          refreshMenuItem?.isVisible = true
          currentSelectedMenuItem?.let { displayPostsOfCategory(categoryId = it.itemId) }
        }
      }

      homeActivityBinding.drawerLayout.closeDrawers()

      return@setNavigationItemSelectedListener true
    }
  }

  /**
   * Choose the view to display.
   * Two views can be displayed:
   *
   * - **Archives View** used to display old posts in an infinite scroll view.
   * - **Posts View** used to display posts of the selected category.
   */
  private fun chooseViewToDisplay(menuItem: MenuItem) {
    archivePostView.isVisible = menuItem.isArchive()
    postListView.isVisible = archivePostView.isNotVisible
  }

  private fun MenuItem.isArchive(): Boolean = this.itemId == R.id.archive_menu_item

  override fun onResume() {
    super.onResume()
    registerReceiver(connectivityStatusMonitor, intentFilter)
  }

  override fun onPause() {
    super.onPause()
    unregisterReceiver(connectivityStatusMonitor)
  }

  override fun onDestroy() {
    super.onDestroy()
    homePresenter.onDestroy()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.home, menu)
    refreshMenuItem = menu.findItem(R.id.refresh_menu_item)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle item selection
    return when (item.itemId) {
      R.id.refresh_menu_item -> {
        homePresenter.refreshData()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun showProgressBar() {
    postListView.showProgressBar()
  }

  override fun hideProgressBar() {
    postListView.hideProgressBar()
  }

  /**
   * Displays the [Post]s of the [Category] with the given [categoryId].
   *
   * @param categoryId id of the category to display.
   */
  private fun displayPostsOfCategory(categoryId: Int) {
    val posts = homePresenter.getPostsByCategory(categoryId)
    if (posts.isNotEmpty()) {
      postListView.updateDisplayedPosts(categoryId, posts)
    }
  }

  /**
   * Displays the given [categories] in the Drawer Menu.
   *
   * @param categories list of [categories] to display.
   */
  private fun displayCategories(categories: List<CategoryIdAndName>) {
    currentSelectedMenuItem = menuManager.generateMenuFromCategories(categories)
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

  companion object {
    private const val DEFAULT_SELECTED_MENU_ITEM_INDEX = 2
  }
}
