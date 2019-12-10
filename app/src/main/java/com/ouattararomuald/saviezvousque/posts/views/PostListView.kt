package com.ouattararomuald.saviezvousque.posts.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.customviews.FeedItem
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.post_list_view.view.posts_recycler_view

/** Displays a fixed list of [Post]s. */
class PostListView : FrameLayout {

  private val postSection = Section().apply { setPlaceholder(NoDataPlaceholder()) }
  private val groupAdapter = GroupAdapter<GroupieViewHolder>()

  private val posts = mutableListOf<PostWithCategory>()
  private var currentCategoryId: Int = -1

  constructor(context: Context) : super(context) {
    initialize()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    initialize()
  }

  private fun initialize() {
    inflate(context, R.layout.post_list_view, this)

    groupAdapter.add(postSection)

    posts_recycler_view.apply {
      layoutManager = LinearLayoutManager(context).apply {
        stackFromEnd = true
      }
      setHasFixedSize(true)
      adapter = groupAdapter
    }
  }

  /**
   * Updates the UI with the given [posts].
   *
   * @param categoryId id of the category the [posts] belong to.
   * @param posts [posts] to be displayed.
   */
  fun updateDisplayedPosts(categoryId: Int, posts: List<PostWithCategory>) {
    this.posts.apply {
      clear()
      addAll(posts)
    }
    currentCategoryId = categoryId
    postSection.update(posts.toPostItems())
  }

  private fun List<PostWithCategory>.toPostItems(): List<FeedItem> = map { it.toPostItem() }

  private fun PostWithCategory.toPostItem(): FeedItem = FeedItem(post = this)
}