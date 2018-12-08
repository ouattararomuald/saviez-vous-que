package com.ouattararomuald.saviezvousque.posts

import com.ouattararomuald.saviezvousque.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class EmptyPostItem : Item<ViewHolder>() {

  override fun getLayout(): Int = R.layout.empty_post_item

  override fun bind(viewHolder: ViewHolder, position: Int) {
  }
}