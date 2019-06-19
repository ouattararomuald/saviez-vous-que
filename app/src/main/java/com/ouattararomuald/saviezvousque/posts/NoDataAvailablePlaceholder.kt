package com.ouattararomuald.saviezvousque.posts

import com.ouattararomuald.saviezvousque.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class NoDataAvailablePlaceholder : Item<ViewHolder>() {

  override fun getLayout(): Int = R.layout.no_data_available_placeholder

  override fun bind(viewHolder: ViewHolder, position: Int) {
  }
}