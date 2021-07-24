package com.ouattararomuald.saviezvousque.posts.theme

import android.content.Context
import android.graphics.Color
import android.view.View
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.databinding.ThemePickerDialogRowBinding
import com.xwray.groupie.viewbinding.BindableItem

class ThemeItem(val context: Context, val themeData: ThemeData) : BindableItem<ThemePickerDialogRowBinding>() {

  override fun getLayout(): Int = R.layout.theme_picker_dialog_row

  override fun initializeViewBinding(view: View): ThemePickerDialogRowBinding {
    return ThemePickerDialogRowBinding.bind(view)
  }


  override fun bind(viewBinding: ThemePickerDialogRowBinding, position: Int) {
    with(viewBinding) {
      colorPrimaryFL.setBackgroundColor(Color.parseColor(themeData.colorPrimary))
      colorPrimaryDarkFL.setBackgroundColor(Color.parseColor(themeData.colorPrimaryDark))
      colorAccentFL.setBackgroundColor(Color.parseColor(themeData.colorAccent))
      themeName.text = themeData.name
    }
  }

  fun getThemeStyle(): Int = ThemeStyleFactory.getStyle(context, themeData.name)
}
