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

internal class ThemeDialogPicker(private val context: Context, onThemeSelectedPicker : (themeStyle: Int, themeName: String) -> Unit) {

  private var themeNames: Array<String> = context.resources.getStringArray(R.array.theme_name_choice_array)
  private var themeColorsArrays: Array<Array<String>> = arrayOf(
      context.resources.getStringArray(R.array.blue_colors_choice_array),
      context.resources.getStringArray(R.array.cyan_colors_choice_array),
      context.resources.getStringArray(R.array.deep_purple_colors_choice_array),
      context.resources.getStringArray(R.array.indigo_colors_choice_array),
      context.resources.getStringArray(R.array.pink_colors_choice_array),
      context.resources.getStringArray(R.array.teal_colors_choice_array),
      context.resources.getStringArray(R.array.yellow_colors_choice_array)
  )
  private val themeDatas: Array<ThemeData>
  private val themesSection = Section()
  private val themesGroupAdapter = GroupAdapter<ViewHolder>().apply { add(themesSection) }

  private val dialogView = LayoutInflater.from(context)
      .inflate(R.layout.theme_selector_dialog, null, false)
  private val themesRecyclerView = dialogView.findViewById<RecyclerView>(R.id.themesRecyclerView).apply {
    layoutManager = LinearLayoutManager(context)
    setHasFixedSize(true)
    adapter = themesGroupAdapter
  }

  private val alertDialogBuilder = AlertDialog.Builder(context).apply {
    setTitle("Select a Theme")
    setView(dialogView)
  }
  private val alertDialog = alertDialogBuilder.create()

  init {
    themeDatas = arrayOf(
        ThemeData(themeNames[0], themeColorsArrays[0][0], themeColorsArrays[0][1], themeColorsArrays[0][2]),
        ThemeData(themeNames[1], themeColorsArrays[1][0], themeColorsArrays[1][1], themeColorsArrays[1][2]),
        ThemeData(themeNames[2], themeColorsArrays[2][0], themeColorsArrays[2][1], themeColorsArrays[2][2]),
        ThemeData(themeNames[3], themeColorsArrays[3][0], themeColorsArrays[3][1], themeColorsArrays[3][2]),
        ThemeData(themeNames[4], themeColorsArrays[4][0], themeColorsArrays[4][1], themeColorsArrays[4][2]),
        ThemeData(themeNames[5], themeColorsArrays[5][0], themeColorsArrays[5][1], themeColorsArrays[5][2]),
        ThemeData(themeNames[6], themeColorsArrays[6][0], themeColorsArrays[6][1], themeColorsArrays[6][2])
    )
    themesSection.addAll(themeDatas.map {
      ThemeItem(context, it)
    })
    themesGroupAdapter.setOnItemClickListener { item, view ->
      if (item is ThemeItem) {
        onThemeSelectedPicker(item.getThemeStyle(), item.themeData.name)
      }
    }
  }

  fun show() {
    alertDialog.show()
  }
}