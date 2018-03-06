package com.ouattararomuald.saviezvousque.posts

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.HomeActivityBinding
import com.ouattararomuald.saviezvousque.db.DbComponent
import com.ouattararomuald.saviezvousque.downloaders.DownloaderComponent
import com.ouattararomuald.saviezvousque.util.getDbComponent
import com.ouattararomuald.saviezvousque.util.getDownloaderComponent
import com.ouattararomuald.saviezvousque.util.toDrawerItem
import javax.inject.Inject

/**
 * Home Page.
 *
 * Displays the different categories and allow users to navigate between them.
 */
class HomeActivity : AppCompatActivity(), ViewContract {

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
  private lateinit var drawer: Drawer

  /** Handles click on item in the [drawer]. */
  private val drawerItemClickHandler = Drawer.OnDrawerItemClickListener { _, _, drawerItem ->
    viewModel.onCategorySelected(drawerItem.identifier.toInt())
    false
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    homeActivityBinding = DataBindingUtil.setContentView(this, R.layout.home_activity)

    val toolbar = homeActivityBinding.toolbar
    setSupportActionBar(toolbar)

    val header = AccountHeaderBuilder().withActivity(this)
        .addProfiles(
            ProfileDrawerItem().withName("Romuald OUATTARA")
                .withEmail("Email@gmail.com")
                .withIcon(R.drawable.ic_rss_feed)
        )
        .build()

    drawer = DrawerBuilder().withActivity(this)
        .withToolbar(toolbar)
        .withHasStableIds(true)
        .withAccountHeader(header)
        .withSavedInstance(savedInstanceState)
        .withCloseOnClick(true)
        .build()

    dbComponent = getDbComponent()
    downloaderComponent = getDownloaderComponent()

    DaggerHomeActivityInjectorComponent.builder()
        .databaseComponent(dbComponent)
        .downloaderComponent(downloaderComponent)
        .activity(this)
        .viewContract(this)
        .build()
        .inject(this)

    drawer.onDrawerItemClickListener = drawerItemClickHandler

    initializeBinding()
    configureRecyclerView()
  }

  private fun configureRecyclerView() {
    feedAdapter = FeedAdapter(viewModel.displayedPosts)
    homeActivityBinding.postsRecyclerView.layoutManager = LinearLayoutManager(this)
    homeActivityBinding.postsRecyclerView.setHasFixedSize(true)
    homeActivityBinding.postsRecyclerView.adapter = feedAdapter
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

  override fun onCategoriesDownloaded(categories: List<Category>) {
    createDrawerItemsFromCategories(categories)
  }

  private fun createDrawerItemsFromCategories(categories: List<Category>) {
    if (categories.isNotEmpty()) {
      drawer.drawerItems.clear()
      val drawerTextColor = ContextCompat.getColor(this, R.color.drawer_item_text_color)
      val drawerSelectedTextColor = ContextCompat.getColor(
          this, R.color.drawer_item_selected_text_color
      )
      categories.forEach {
        drawer.addItem(it.toDrawerItem(drawerTextColor, drawerSelectedTextColor))
      }
    }
  }

  override fun onPostsDownloaded(posts: List<Post>) {
    TODO("not implemented")
  }

  override fun notifyDatasetChanged() {
    feedAdapter.notifyDataSetChanged()
  }
}
