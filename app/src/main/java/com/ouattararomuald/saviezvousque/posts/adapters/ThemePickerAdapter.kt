package com.ouattararomuald.saviezvousque.posts.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.ouattararomuald.saviezvousque.R

class ThemePickerAdapter(
  context: Context,
  private val data: Array<ThemeData>
) : ArrayAdapter<ThemeData>(context, R.layout.theme_picker_dialog_row, data) {

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val inflater = context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val rootView = convertView ?: inflater.inflate(R.layout.theme_picker_dialog_row, parent, false)

    val colorPrimaryFrameLayout = rootView.findViewById<FrameLayout>(R.id.colorPrimaryFL)
    val colorPrimaryDarkFrameLayout = rootView.findViewById<FrameLayout>(R.id.colorPrimaryDarkFL)
    val colorAccentFrameLayout = rootView.findViewById<FrameLayout>(R.id.colorAccentFL)
    val themeNameTextView = rootView.findViewById<AppCompatTextView>(R.id.themeName)

    data[position].run {
      colorPrimaryFrameLayout.setBackgroundColor(Color.parseColor(this.colorPrimary))
      colorPrimaryDarkFrameLayout.setBackgroundColor(Color.parseColor(this.colorPrimaryDark))
      colorAccentFrameLayout.setBackgroundColor(Color.parseColor(this.colorAccent))
      themeNameTextView.text = this.name
    }

    return rootView
  }
}