package com.ouattararomuald.saviezvousque.posts.archives

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.adapters.FeedItemViewHolder
import com.ouattararomuald.saviezvousque.databinding.FeedItemViewBinding
import com.ouattararomuald.saviezvousque.db.DateTimeConverter
import com.ouattararomuald.saviezvousque.db.PostWithCategory

class PagedPostAdapter : PagedListAdapter<Post, FeedItemViewHolder>(PostDiffUtilCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedItemViewHolder {
    val itemBinding: FeedItemViewBinding = FeedItemViewBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )

    return FeedItemViewHolder(itemBinding)
  }

  override fun onBindViewHolder(holder: FeedItemViewHolder, position: Int) {
    val post = getItem(position)

    if (post != null) {
      holder.bind(post.toPostWithCategory())
    }
  }

  private fun Post.toPostWithCategory(): PostWithCategory {
    return PostWithCategory(
        this.id,
        this.title.value,
        this.content.value,
        this.getImageUrl(),
        this.categoryId,
        "",
        "",
        DateTimeConverter.toLocalDateTime(this.publicationDateUtc),
        DateTimeConverter.toLocalDateTime(this.lastUpdateUtc)
    )
  }
}
