package com.ouattararomuald.saviezvousque.posts.archives

import androidx.recyclerview.widget.DiffUtil
import com.ouattararomuald.saviezvousque.common.Post

object PostDiffUtilCallback : DiffUtil.ItemCallback<Post>() {
  override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
}