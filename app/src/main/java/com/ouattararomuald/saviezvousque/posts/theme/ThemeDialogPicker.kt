package com.ouattararomuald.saviezvousque.posts.theme

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ouattararomuald.saviezvousque.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder

internal class ThemeDialogPicker(
  private val context: Context,
  onThemeSelectedListener: (themeStyle: Int, themeName: String) -> Unit
) {

  private var themeNames: Array<String> =
      context.resources.getStringArray(R.array.theme_name_choice_array)
  private var themeColorsArrays: Array<Array<String>> = arrayOf(
      context.resources.getStringArray(R.array.blue_colors_choice_array),
      context.resources.getStringArray(R.array.cyan_colors_choice_array),
      context.resources.getStringArray(R.array.deep_purple_colors_choice_array),
      context.resources.getStringArray(R.array.indigo_colors_choice_array),
      context.resources.getStringArray(R.array.pink_colors_choice_array),
      context.resources.getStringArray(R.array.teal_colors_choice_array),
      context.resources.getStringArray(R.array.yellow_colors_choice_array)
  )
  private val themesData: Array<ThemeData>
  private val themesSection = Section()
  private val themesGroupAdapter = GroupAdapter<ViewHolder>().apply { add(themesSection) }

  private var dialogView = LayoutInflater.from(context).inflate(
      R.layout.theme_selector_dialog, null, false
  )

  private val alertDialogBuilder = AlertDialog.Builder(context).apply {
    setTitle(context.getString(R.string.select_a_theme))
    setView(dialogView)
  }
  private val alertDialog = alertDialogBuilder.create()

  init {
    dialogView.findViewById<RecyclerView>(R.id.themesRecyclerView).apply {
      layoutManager = LinearLayoutManager(context)
      setHasFixedSize(true)
      adapter = themesGroupAdapter
    }

    themesData = getThemesData(numberOfThemes = themeNames.size)
    themesSection.addAll(themesData.map {
      ThemeItem(context, it)
    })
    themesGroupAdapter.setOnItemClickListener { item, _ ->
      if (item is ThemeItem) {
        onThemeSelectedListener(item.getThemeStyle(), item.themeData.name)
      }
    }
  }

  private fun getThemesData(numberOfThemes: Int): Array<ThemeData> {
    val themes = mutableListOf<ThemeData>()

    repeat(numberOfThemes) { index ->
      themes.add(ThemeData(
          themeNames[index],
          themeColorsArrays.getThemeColors(index).getColorPrimary(),
          themeColorsArrays.getThemeColors(index).getColorPrimaryDark(),
          themeColorsArrays.getThemeColors(index).getColorAccent()
      ))
    }

    return themes.toTypedArray()
  }

  private fun Array<Array<String>>.getThemeColors(position: Int): Array<String> = this[position]

  private fun Array<String>.getColorPrimary(): String = this[COLOR_PRIMARY_INDEX]

  private fun Array<String>.getColorPrimaryDark(): String = this[COLOR_PRIMARY_DARK_INDEX]

  private fun Array<String>.getColorAccent(): String = this[COLOR_ACCENT_INDEX]

  fun show() {
    alertDialog.show()
  }

  companion object {
    private const val COLOR_PRIMARY_INDEX = 0
    private const val COLOR_PRIMARY_DARK_INDEX = 1
    private const val COLOR_ACCENT_INDEX = 2
  }
}