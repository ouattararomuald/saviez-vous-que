package com.ouattararomuald.saviezvousque.customviews

import androidx.recyclerview.widget.RecyclerView
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.FeedItemViewBinding

class FeedItemViewHolder(
    private val itemBinding: FeedItemViewBinding
) : RecyclerView.ViewHolder(itemBinding.root) {

  /**
   * Binds the given [post] with this [FeedItemViewHolder].
   *
   * @param post [Post] to bind to this [FeedItemViewHolder].
   */
  fun bind(post: Post) {
    itemBinding.post = post
    itemBinding.executePendingBindings()
  }
}
