package com.ouattararomuald.saviezvousque.posts

import android.databinding.DataBindingUtil
import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.PostItemViewBinding

internal class FeedAdapter(
  private val observablePostsList: ObservableList<Post>
) : RecyclerView.Adapter<PostViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
    val itemBinding: PostItemViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(parent.context), R.layout.post_item_view, parent, false
    )

    return PostViewHolder(itemBinding)
  }

  override fun getItemCount(): Int = observablePostsList.size

  override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
    holder.bind(getPostAtPosition(position))
  }

  private fun getPostAtPosition(position: Int): Post = observablePostsList[position]
}
