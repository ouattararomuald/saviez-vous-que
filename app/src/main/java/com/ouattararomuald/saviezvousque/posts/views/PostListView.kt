package com.ouattararomuald.saviezvousque.posts.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.customviews.FeedItem
import com.ouattararomuald.saviezvousque.databinding.PostListViewBinding
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section

/** Displays a fixed list of [Post]s. */
class PostListView : FrameLayout {

  private val postSection = Section().apply { setPlaceholder(NoDataPlaceholder()) }
  private val groupAdapter = GroupAdapter<GroupieViewHolder>()

  private val posts = mutableListOf<PostWithCategory>()
  private var currentCategoryId: Int = -1

  private lateinit var binding: PostListViewBinding

  constructor(context: Context) : super(context) {
    initialize()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    initialize()
  }

  private fun initialize() {
    binding = PostListViewBinding.inflate(LayoutInflater.from(context), this)

    groupAdapter.add(postSection)

    with(binding.postsRecyclerView) {
      layoutManager = LinearLayoutManager(context).apply {
        stackFromEnd = true
      }
      setHasFixedSize(true)
      adapter = groupAdapter
    }
  }

  fun showProgressBar() {
    binding.progressBar.isVisible = true
  }

  fun hideProgressBar() {
    binding.progressBar.isGone = true
  }

  /**
   * Updates the UI with the given [posts].
   *
   * @param categoryId id of the category the [posts] belong to.
   * @param posts [posts] to be displayed.
   */
  fun updateDisplayedPosts(categoryId: Int, posts: List<PostWithCategory>) {
    with(this.posts) {
      clear()
      addAll(posts)
    }
    currentCategoryId = categoryId
    postSection.update(posts.toPostItems())
  }

  private fun List<PostWithCategory>.toPostItems(): List<FeedItem> = map { it.toPostItem() }

  private fun PostWithCategory.toPostItem(): FeedItem = FeedItem(post = this)
}
