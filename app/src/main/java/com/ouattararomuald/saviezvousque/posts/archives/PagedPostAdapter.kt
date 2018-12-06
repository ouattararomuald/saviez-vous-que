package com.ouattararomuald.saviezvousque.posts.archives

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.PostItemViewBinding
import com.ouattararomuald.saviezvousque.posts.PostViewHolder

class PagedPostAdapter : PagedListAdapter<Post, PostViewHolder>(PostDiffUtilCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
    val itemBinding: PostItemViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(parent.context), R.layout.post_item_view, parent, false
    )

    return PostViewHolder(itemBinding)
  }

  override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
    val post = getItem(position)

    if (post != null) {
      holder.bind(post)
    }
  }
}