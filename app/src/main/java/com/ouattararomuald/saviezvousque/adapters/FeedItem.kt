package com.ouattararomuald.saviezvousque.customviews

import android.view.View
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.databinding.FeedItemViewBinding
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.xwray.groupie.viewbinding.BindableItem

class FeedItem(private val post: PostWithCategory) : BindableItem<FeedItemViewBinding>() {

  override fun getLayout(): Int = R.layout.feed_item_view

  override fun initializeViewBinding(view: View): FeedItemViewBinding {
    return FeedItemViewBinding.bind(view)
  }

  override fun bind(binding: FeedItemViewBinding, position: Int) {
    binding.feedItemView.postTitle = post.title
    binding.feedItemView.postImageUrl = post.imageUrl ?: ""
  }
}
