package com.ouattararomuald.saviezvousque.util

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category

internal fun Category.toDrawerItem(): PrimaryDrawerItem {
  return PrimaryDrawerItem().withIdentifier(id.toLong())
      .withName(name)
      .withIcon(R.drawable.ic_rss_feed)
}
