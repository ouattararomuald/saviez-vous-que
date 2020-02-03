package com.ouattararomuald.saviezvousque.posts.views

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.TestsUtil
import com.ouattararomuald.saviezvousque.customviews.FeedItem
import com.xwray.groupie.GroupAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class PostListViewTest {

  private lateinit var postListView: PostListView

  @Before fun setUp() {
    val activityController = Robolectric.buildActivity(Activity::class.java)

    postListView = LayoutInflater.from(activityController.get())
        .inflate(R.layout.post_list_view, PostListView(RuntimeEnvironment.application),
            true) as PostListView
  }

  @Test fun verifyInitialState() {
    assertThat(postListView.isVisible).isTrue()

    val postsRecyclerView = postListView.findViewById<RecyclerView>(R.id.posts_recycler_view)
    val groupAdapter = postsRecyclerView.adapter as GroupAdapter<*>?
    val linearLayoutManager = postsRecyclerView.layoutManager as LinearLayoutManager

    assertThat(groupAdapter).isNotNull()
    assertThat(groupAdapter).isInstanceOf(GroupAdapter::class.java)
    assertThat(groupAdapter?.itemCount).isEqualTo(1)
    assertThat(groupAdapter?.getItem(0)).isInstanceOf(NoDataPlaceholder::class.java)
    assertThat(linearLayoutManager.stackFromEnd).isTrue()
    assertThat(postsRecyclerView.isEmpty()).isTrue()
    assertThat(postsRecyclerView.hasFixedSize()).isTrue()
  }

  @Test fun updateDisplayedPosts() {
    assertThat(postListView.isVisible).isTrue()

    val numberOfViews = 10
    val category = TestsUtil.generateCategories(quantity = 1).first()
    val posts = TestsUtil.generatePosts(numberOfViews)

    val postsRecyclerView = postListView.findViewById<RecyclerView>(R.id.posts_recycler_view)
    val groupAdapter = postsRecyclerView.adapter as GroupAdapter<*>?

    postListView.updateDisplayedPosts(category.id, posts)

    assertThat(groupAdapter?.itemCount).isEqualTo(numberOfViews)
    repeat(groupAdapter!!.itemCount) {
      assertThat(groupAdapter.getItem(it)).isInstanceOf(FeedItem::class.java)
    }
    assertThat(postsRecyclerView.isEmpty()).isTrue()

    postListView.updateDisplayedPosts(category.id, emptyList())

    assertThat(postsRecyclerView.isEmpty()).isTrue()
  }
}