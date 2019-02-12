package com.ouattararomuald.saviezvousque.posts.theme

import android.content.Context
import com.ouattararomuald.saviezvousque.R

object ThemeStyleFactory {

  fun getStyle(context: Context, themeName: String): Int {
    val themes = context.resources.getStringArray(R.array.theme_name_choice_array)
    return when (themeName) {
      themes[0] -> R.style.AppTheme_NoActionBar_BlueTheme
      themes[1] -> R.style.AppTheme_NoActionBar_CyanTheme
      themes[2] -> R.style.AppTheme_NoActionBar_DeepPurpleTheme
      themes[3] -> R.style.AppTheme_NoActionBar_IndigoTheme
      themes[4] -> R.style.AppTheme_NoActionBar_PinkTheme
      themes[5] -> R.style.AppTheme_NoActionBar_TealTheme
      themes[6] -> R.style.AppTheme_NoActionBar_YellowTheme
      else -> R.style.AppTheme_NoActionBar_DeepPurpleTheme
    }
  }
}