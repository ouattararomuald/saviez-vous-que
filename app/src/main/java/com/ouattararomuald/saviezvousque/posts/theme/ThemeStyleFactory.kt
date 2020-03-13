package com.ouattararomuald.saviezvousque.posts.theme

import android.content.Context
import com.ouattararomuald.saviezvousque.R

object ThemeStyleFactory {

  private const val BLUE_THEME_INDEX = 0
  private const val CYAN_THEME_INDEX = 1
  private const val DEEP_PURPLE_THEME_INDEX = 2
  private const val INDIGO_THEME_INDEX = 3
  private const val PINK_THEME_INDEX = 4
  private const val TEAL_THEME_INDEX = 5
  private const val YELLOW_THEME_INDEX = 6

  fun getStyle(context: Context, themeName: String): Int {
    val themes = context.resources.getStringArray(R.array.theme_name_choice_array)
    return when (themeName) {
      themes[BLUE_THEME_INDEX] -> R.style.AppTheme_NoActionBar_BlueTheme
      themes[CYAN_THEME_INDEX] -> R.style.AppTheme_NoActionBar_CyanTheme
      themes[DEEP_PURPLE_THEME_INDEX] -> R.style.AppTheme_NoActionBar_DeepPurpleTheme
      themes[INDIGO_THEME_INDEX] -> R.style.AppTheme_NoActionBar_IndigoTheme
      themes[PINK_THEME_INDEX] -> R.style.AppTheme_NoActionBar_PinkTheme
      themes[TEAL_THEME_INDEX] -> R.style.AppTheme_NoActionBar_TealTheme
      themes[YELLOW_THEME_INDEX] -> R.style.AppTheme_NoActionBar_YellowTheme
      else -> R.style.AppTheme_NoActionBar_DeepPurpleTheme
    }
  }
}
