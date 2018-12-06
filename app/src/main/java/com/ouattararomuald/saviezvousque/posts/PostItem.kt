package com.ouattararomuald.saviezvousque.posts

import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.PostItemViewBinding
import com.xwray.groupie.databinding.BindableItem

class PostItem(private val post: Post) : BindableItem<PostItemViewBinding>() {

  override fun getLayout(): Int = R.layout.post_item_view

  override fun bind(binding: PostItemViewBinding, position: Int) {
    binding.post = post
  }
}