package com.ouattararomuald.saviezvousque.posts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.HomeActivityBinding
import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent
import com.ouattararomuald.saviezvousque.posts.archives.PaginatedPostView
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
  @Inject
  lateinit var viewModel: HomeViewModel

  private lateinit var sharedViewModel: SharedViewModel

  private lateinit var homeActivityBinding: HomeActivityBinding

  /** Dagger database component. */
  private lateinit var dbComponent: DbComponent

  /** Downloader component. */
  private lateinit var downloaderComponent: DownloaderComponent

  private lateinit var navigationView: NavigationView

  private lateinit var archivePostView: PaginatedPostView
  private lateinit var postsView: PostView

  private var previousSelectedMenuItem: MenuItem? = null
  private var currentSelectedMenuItem: MenuItem? = null

  private val intentFilter: IntentFilter = IntentFilter().apply {
    @Suppress("DEPRECATION")
    addAction(ConnectivityManager.CONNECTIVITY_ACTION)
  }
  private val connectivityStatusMonitor = ConnectivityStatusMonitor()

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
    archivePostView = homeActivityBinding.drawerLayout.content_home.paginated_post_view
    postsView = homeActivityBinding.drawerLayout.content_home.posts_view

    navigationView.setNavigationItemSelectedListener { menuItem ->
      currentSelectedMenuItem = menuItem

      menuItem.isChecked = true
      toolbar.title = menuItem.title

      archivePostView.isVisible = menuItem.itemId == R.id.archive_menu_item
      postsView.isVisible = !archivePostView.isVisible

      if (menuItem.itemId == R.id.archive_menu_item) {
        archivePostView.configureDataSourceFactory(
            getDownloaderComponent().feedDownloader(),
            getDbComponent().feedRepository(),
            this
        )
      } else {
        currentSelectedMenuItem?.let { updateSelectedPosts(it.itemId) }
      }

      homeActivityBinding.drawerLayout.closeDrawers()

      return@setNavigationItemSelectedListener true
    }

    dbComponent = getDbComponent()
    downloaderComponent = getDownloaderComponent()

    DaggerHomeActivityInjectorComponent.builder()
        .databaseComponent(dbComponent)
        .downloaderComponent(downloaderComponent)
        .activity(this)
        .build()
        .inject(this)

    initializeBinding()

    val categoriesObserver = Observer<List<Category>> {
      displayCategories(it)
    }

    viewModel.categories.observe(this, categoriesObserver)

    val postsObserver = Observer<MutableMap<Int, List<Post>>> {
      currentSelectedMenuItem?.let { updateSelectedPosts(it.itemId) }
    }

    viewModel.posts.observe(this, postsObserver)
    sharedViewModel.networkState.observe(this, Observer {
      if (it == NetworkState.AVAILABLE) {
        viewModel.refreshData()
      }
    })
  }

  private fun initializeBinding() {
    homeActivityBinding.viewModel = viewModel
  }

  override fun onResume() {
    super.onResume()
    registerReceiver(connectivityStatusMonitor, intentFilter)
  }

  override fun onPause() {
    super.onPause()
    unregisterReceiver(connectivityStatusMonitor)
  }

  /*override fun onSupportNavigateUp(): Boolean = NavigationUI.navigateUp(
      findNavController(R.id.nav_host_fragment),
      homeActivityBinding.drawerLayout
  )*/

  /*override fun onNavigationItemSelected(item: MenuItem): Boolean {
    previousSelectedMenuItem = currentSelectedMenuItem
    currentSelectedMenuItem = item

    if (item.itemId == R.id.archive_menu_item) {
      findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_archivesFragment)
    } else {
      if (previousSelectedMenuItem?.itemId == R.id.archive_menu_item
          && currentSelectedMenuItem?.itemId != R.id.archive_menu_item) {

      }
      currentSelectedMenuItem?.let { updateSelectedPosts(it.itemId) }
    }

    item.isChecked = true
    toolbar.title = item.title
    homeActivityBinding.drawerLayout.closeDrawers()
    return true
  }*/

  /*override fun onBackPressed() {
    if (isArchiveMenuSelected()) {
      val temp = currentSelectedMenuItem
      currentSelectedMenuItem = previousSelectedMenuItem
      previousSelectedMenuItem = temp
      //currentSelectedMenuItem?.itemId = sharedViewModel.categoryId.value ?: -1

      currentSelectedMenuItem?.let {
        //val menu = navigationView.menu
        //menu.findItem(it.itemId).isChecked = true
        it.isChecked = true
        toolbar.title = it.title
      }
      //onSupportNavigateUp()
    } else {
      super.onBackPressed()
    }
  }*/

  /*override fun onBackPressed() {
    findNavController(R.id.nav_host_fragment).popBackStack()
  }*/

  private fun isArchiveMenuSelected(): Boolean = currentSelectedMenuItem?.itemId == R.id.archive_menu_item

  private fun updateSelectedPosts(categoryId: Int) {
    //if (sharedViewModel.categoryId.value != categoryId && categoryId >= 0) {
    val posts = viewModel.getPostsByCategory(categoryId)
    if (posts.isNotEmpty()) {
      sharedViewModel.categoryId.postValue(categoryId)
      sharedViewModel.posts.postValue(posts)
      postsView.displayPosts(categoryId, posts)
    }
    //}
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
      menu.getItem(1).isChecked = true
      currentSelectedMenuItem = menu.getItem(1)
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
