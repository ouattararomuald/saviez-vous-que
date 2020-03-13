package com.ouattararomuald.saviezvousque.posts.theme

import android.content.Context
import android.graphics.Color
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.posts.theme.ThemeData
import com.ouattararomuald.saviezvousque.posts.theme.ThemeStyleFactory
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.theme_picker_dialog_row.colorAccentFL
import kotlinx.android.synthetic.main.theme_picker_dialog_row.colorPrimaryDarkFL
import kotlinx.android.synthetic.main.theme_picker_dialog_row.colorPrimaryFL
import kotlinx.android.synthetic.main.theme_picker_dialog_row.themeName

class ThemeItem(val context: Context, val themeData: ThemeData) : Item() {

  override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    viewHolder.colorPrimaryFL.setBackgroundColor(Color.parseColor(themeData.colorPrimary))
    viewHolder.colorPrimaryDarkFL.setBackgroundColor(Color.parseColor(themeData.colorPrimaryDark))
    viewHolder.colorAccentFL.setBackgroundColor(Color.parseColor(themeData.colorAccent))
    viewHolder.themeName.text = themeData.name
  }

  override fun getLayout(): Int = R.layout.theme_picker_dialog_row

  fun getThemeStyle(): Int = ThemeStyleFactory.getStyle(context, themeData.name)
}
