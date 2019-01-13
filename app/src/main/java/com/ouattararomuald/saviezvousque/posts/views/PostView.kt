package com.ouattararomuald.saviezvousque.posts.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.posts.EmptyPostItem
import com.ouattararomuald.saviezvousque.posts.PostItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.post_view.view.postsRecyclerView

class PostView: FrameLayout {

  private var postSection = Section().apply { setPlaceholder(EmptyPostItem()) }
  private val groupAdapter = GroupAdapter<ViewHolder>()

  private var categoryId: Int = -1
  private val posts = mutableListOf<Post>()

  constructor(context: Context) : super(context) { initialize() }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { initialize() }

  private fun initialize() {
    inflate(context, R.layout.post_view, this)

    groupAdapter.add(postSection)

    postsRecyclerView.apply {
      layoutManager = LinearLayoutManager(context).apply {
        reverseLayout = true
      }
      setHasFixedSize(true)
      adapter = groupAdapter
    }
  }

  /**
   * Displays the given [posts].
   *
   * @posts categoryId id of the category the [posts] belong to.
   * @posts posts to be displayed.
   */
  fun displayPosts(categoryId: Int, posts: List<Post>) {
    this.posts.apply {
      clear()
      addAll(posts)
    }
    this.categoryId = categoryId
    postSection.update(posts.map { post -> PostItem(post) })
  }
}