package com.ouattararomuald.saviezvousque.posts

import android.support.v7.widget.RecyclerView
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.PostItemViewBinding

class PostViewHolder(
    private val itemBinding: PostItemViewBinding
) : RecyclerView.ViewHolder(itemBinding.root) {

  /**
   * Binds the given [post] with this [PostViewHolder].
   *
   * @param post [Post] to bind to this [PostViewHolder].
   */
  fun bind(post: Post) {
    itemBinding.post = post
    itemBinding.executePendingBindings()
  }
}