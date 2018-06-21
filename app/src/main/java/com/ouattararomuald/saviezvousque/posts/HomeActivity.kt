package com.ouattararomuald.saviezvousque.posts

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.materialdrawer.Drawer
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.HomeActivityBinding
import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent
import com.ouattararomuald.saviezvousque.util.getDbComponent
import com.ouattararomuald.saviezvousque.util.getDownloaderComponent
import kotlinx.android.synthetic.main.home_activity.nav_view
import kotlinx.android.synthetic.main.home_app_bar.toolbar
import kotlinx.android.synthetic.main.home_content.posts_recycler_view
import javax.inject.Inject

/**
 * Home Page.
 *
 * Displays the different categories and allow users to navigate between them.
 */
class HomeActivity : AppCompatActivity(), ViewContract,
    NavigationView.OnNavigationItemSelectedListener {

  /** The [HomeViewModel] bound to this activity. */
  @Inject
  lateinit var viewModel: HomeViewModel

  private lateinit var homeActivityBinding: HomeActivityBinding
  private lateinit var feedAdapter: FeedAdapter

  /** Dagger database component. */
  private lateinit var dbComponent: DbComponent
  /** Downloader component. */
  private lateinit var downloaderComponent: DownloaderComponent

  /** [Drawer] that displays the different categories as a list in the navigation drawer. */
  // private lateinit var drawer: Drawer

  private lateinit var postsRecyclerView: RecyclerView
  private lateinit var navigationView: NavigationView

  /** Handles click on item in the [drawer]. */
  private val drawerItemClickHandler = Drawer.OnDrawerItemClickListener { _, _, drawerItem ->
    viewModel.onCategorySelected(drawerItem.identifier.toInt())
    false
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    homeActivityBinding = DataBindingUtil.setContentView(this, R.layout.home_activity)

    setSupportActionBar(toolbar)

    val toggle = ActionBarDrawerToggle(
        this,
        homeActivityBinding.drawerLayout,
        toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close
    )
    homeActivityBinding.drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    navigationView = nav_view
    postsRecyclerView = posts_recycler_view

    navigationView.setNavigationItemSelectedListener(this)

    dbComponent = getDbComponent()
    downloaderComponent = getDownloaderComponent()

    DaggerHomeActivityInjectorComponent.builder()
        .databaseComponent(dbComponent)
        .downloaderComponent(downloaderComponent)
        .activity(this)
        .viewContract(this)
        .build()
        .inject(this)

    initializeBinding()
    configureRecyclerView()
  }

  private fun configureRecyclerView() {
    feedAdapter = FeedAdapter(viewModel.displayedPosts)
    postsRecyclerView.layoutManager = LinearLayoutManager(this)
    postsRecyclerView.setHasFixedSize(true)
    postsRecyclerView.adapter = feedAdapter
  }

  private fun initializeBinding() {
    homeActivityBinding.viewModel = viewModel
  }

  override fun onResume() {
    super.onResume()
    viewModel.onResume()
  }

  override fun onPause() {
    super.onPause()
    viewModel.onPause()
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    viewModel.onCategorySelected(item.itemId)
    return true
  }

  override fun onCategoriesDownloaded(categories: List<Category>) {
    createDrawerItemsFromCategories(categories)

    /*if (drawer.drawerItems.isNotEmpty()) {
      drawer.drawerItems[0].withSetSelected(true)
      viewModel.onCategorySelected(drawer.drawerItems[0].identifier.toInt())
    }*/
  }

  private fun createDrawerItemsFromCategories(categories: List<Category>) {
    if (categories.isNotEmpty()) {
      val menu = navigationView.menu
      categories.forEach {
        menu.add(R.id.main_group, it.id, Menu.NONE, it.name)
      }
    }
  }

  override fun onPostsDownloaded(posts: List<Post>) {
    //feedAdapter.
  }

  override fun notifyDatasetChanged() {
    feedAdapter.notifyDataSetChanged()
  }
}
