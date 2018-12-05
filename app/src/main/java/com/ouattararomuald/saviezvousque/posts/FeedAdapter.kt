package com.ouattararomuald.saviezvousque.posts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.databinding.PostItemViewBinding

internal class FeedAdapter(
  private val observablePostsList: ObservableList<Post>
) : androidx.recyclerview.widget.RecyclerView.Adapter<PostViewHolder>() {

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
