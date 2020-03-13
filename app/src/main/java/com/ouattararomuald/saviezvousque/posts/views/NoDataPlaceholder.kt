package com.ouattararomuald.saviezvousque.posts.views

import com.ouattararomuald.saviezvousque.R
import com.xwray.groupie.Item
import com.xwray.groupie.GroupieViewHolder

class NoDataPlaceholder : Item<GroupieViewHolder>() {

  override fun getLayout(): Int = R.layout.no_data_available_placeholder

  override fun bind(viewHolder: GroupieViewHolder, position: Int) {
  }
}
