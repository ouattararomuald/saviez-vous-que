package com.ouattararomuald.saviezvousque.adapters

import android.view.View
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.databinding.FeedItemViewBinding
import com.ouattararomuald.saviezvousque.db.PostWithCategory
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class FeedItem(private val post: PostWithCategory) : BindableItem<FeedItemViewBinding>() {

  override fun getId(): Long {
    return post.postId.toLong()
  }

  override fun isSameAs(other: Item<*>): Boolean {
    return id == (other as FeedItem).id
  }

  override fun hasSameContentAs(other: Item<*>): Boolean {
    return post == (other as FeedItem).post
  }

  override fun getLayout(): Int = R.layout.feed_item_view

  override fun initializeViewBinding(view: View): FeedItemViewBinding {
    return FeedItemViewBinding.bind(view)
  }

  override fun bind(binding: FeedItemViewBinding, position: Int) {
    with(binding.feedItemView) {
      postTitle = post.title
      postImageUrl = post.imageUrl ?: ""
    }
  }
}
