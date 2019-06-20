package com.ouattararomuald.saviezvousque.customviews

import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.FeedItemViewBinding
import com.xwray.groupie.databinding.BindableItem

class FeedItem(private val post: Post) : BindableItem<FeedItemViewBinding>() {

  override fun getLayout(): Int = R.layout.feed_item_view

  override fun bind(binding: FeedItemViewBinding, position: Int) {
    binding.post = post
  }
}