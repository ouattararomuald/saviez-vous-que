package com.ouattararomuald.saviezvousque.posts

import android.view.Menu
import android.view.MenuItem
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName

/**
 * Manages a menu.
 *
 * @property menu the menu to managed.
 * @property selectedMenuItemIndex index of the menu item selected by default.
 */
class MenuManager(private val menu: Menu, private val selectedMenuItemIndex: Int) {

  /**
   * Fills the menu by generating [MenuItem] from the given list of [categories].
   *
   * @param categories list of [categories] to display in the drawer.
   * @return the selected [MenuItem] or null if the list of [categories] is empty.
   */
  fun generateMenuFromCategories(categories: List<CategoryIdAndName>): MenuItem? {
    if (categories.isNotEmpty()) {
      categories.forEach { category ->
        if (menu.findItem(category.id) == null) {
          menu.add(R.id.main_group, category.id, Menu.NONE, category.name)
        }
      }

      menu.setGroupCheckable(R.id.main_group, true, true)
      menu.getItem(selectedMenuItemIndex).isChecked = true
      return menu.getItem(selectedMenuItemIndex)
    }

    return null
  }
}
