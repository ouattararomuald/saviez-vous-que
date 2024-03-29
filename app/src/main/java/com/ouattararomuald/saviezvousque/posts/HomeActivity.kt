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
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.navigation.NavigationView
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.customviews.LoadingState
import com.ouattararomuald.saviezvousque.databinding.HomeActivityBinding
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.FeedRepository
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.ouattararomuald.saviezvousque.db.SharedPreferenceManager
import com.ouattararomuald.saviezvousque.downloaders.FeedDownloader
import com.ouattararomuald.saviezvousque.posts.archives.PaginatedPostView
import com.ouattararomuald.saviezvousque.posts.theme.ThemeDialogPicker
import com.ouattararomuald.saviezvousque.posts.theme.ThemeStyleFactory
import com.ouattararomuald.saviezvousque.posts.views.PostListView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Displays the different categories and allow users to navigate between them. */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), HomeContract.View {

  /** The [HomePresenter] bound to this activity. */
  private val homePresenter: HomePresenter by viewModels()

  @Inject lateinit var sharedPreferenceManager: SharedPreferenceManager
  @Inject lateinit var feedDownloader: FeedDownloader
  @Inject lateinit var feedRepository: FeedRepository

  private lateinit var homeActivityBinding: HomeActivityBinding

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

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch { observeCategories() }
        launch { observePosts() }
        launch { observePostsByCategories() }
        launch { observeLoadingState() }
      }
    }

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

    configureNavigationMenuEventClickListener()
  }

  private suspend fun observeCategories() {
    homePresenter.categoriesObservable.collect {
      displayCategories(it)
    }
  }

  private suspend fun observePosts() {
    homePresenter.postsObservable.collect { posts ->
      currentSelectedMenuItem?.let { displayPostsOfCategory(it.itemId, posts) }
    }
  }

  private suspend fun observePostsByCategories() {
    homePresenter.postsByCategoryObservable.collect { posts ->
      currentSelectedMenuItem?.let { displayPostsOfCategory(it.itemId, posts) }
    }
  }

  private suspend fun observeLoadingState() {
    homePresenter.loadingStateObservable.collect {
      when (it) {
        LoadingState.FINISHED -> {
          hideProgressBar()
        }
        LoadingState.LOADING -> {
          showProgressBar()
        }
      }
    }
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
            feedDownloader,
            feedRepository,
            this
          )
        }
        R.id.choose_theme_menu_item -> themeDialogPicker.show()
        else -> {
          refreshMenuItem?.isVisible = true
          currentSelectedMenuItem?.let { homePresenter.categoryClicked(categoryId = it.itemId) }
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

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.home, menu)
    refreshMenuItem = menu.findItem(R.id.refresh_menu_item)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.refresh_menu_item -> {
        homePresenter.refreshData()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun showProgressBar() {
    postListView.showProgressBar()
  }

  private fun hideProgressBar() {
    postListView.hideProgressBar()
  }

  /**
   * Displays the [Post]s of the [Category] with the given [categoryId].
   *
   * @param categoryId id of the category to display.
   * @param posts list of posts that belong to the category.
   */
  private fun displayPostsOfCategory(categoryId: Int, posts: List<PostWithCategory>) {
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
