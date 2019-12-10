package com.ouattararomuald.saviezvousque.customviews

import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.databinding.FeedItemViewBinding
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.xwray.groupie.databinding.BindableItem

class FeedItem(private val post: PostWithCategory) : BindableItem<FeedItemViewBinding>() {

  override fun getLayout(): Int = R.layout.feed_item_view

  override fun bind(binding: FeedItemViewBinding, position: Int) {
    binding.post = post
  }
}