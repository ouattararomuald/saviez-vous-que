package com.ouattararomuald.saviezvousque.adapters

import androidx.recyclerview.widget.RecyclerView
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.FeedItemViewBinding
import com.ouattararomuald.saviezvousque.db.PostWithCategory

class FeedItemViewHolder(
  private val itemBinding: FeedItemViewBinding
) : RecyclerView.ViewHolder(itemBinding.root) {

  /**
   * Binds the given [post] with this [FeedItemViewHolder].
   *
   * @param post [Post] to bind to this [FeedItemViewHolder].
   */
  fun bind(post: PostWithCategory) {
    with(itemBinding.feedItemView) {
      postTitle = post.title
      postImageUrl = post.imageUrl!!
    }
  }
}
