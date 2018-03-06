package com.ouattararomuald.saviezvousque.util

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Category

internal fun Category.toDrawerItem(drawerTextColor: Int, drawerSelectedTextColor: Int)
    : PrimaryDrawerItem = PrimaryDrawerItem().withIdentifier(id.toLong())
    .withName(name)
    .withTextColor(drawerTextColor)
    .withSelectedTextColor(drawerSelectedTextColor)
    .withIcon(R.drawable.ic_rss_feed)
