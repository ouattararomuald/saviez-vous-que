package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.db.Category
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.CategoryQueries
import com.ouattararomuald.saviezvousque.db.exceptions.CategoryIdNotFoundException
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.ouattararomuald.saviezvousque.common.Category as CategoryAdapter

class CategoryDao @Inject constructor(private val categoryQueries: CategoryQueries) {

  fun getCategories(): Flow<List<CategoryIdAndName>> = categoryQueries.categoryIdAndName().asFlow().mapToList()

  /**
   * Creates all the given [categories].
   *
   * Existing categories won't be created.
   *
   * @param categories list of categories to create.
   */
  fun createCategories(categories: List<CategoryAdapter>) {
    val sortedCategories = sortCategories(categories)
    sortedCategories.toCategories().forEachIndexed { displayOrder, category ->
      createCategory(category, displayOrder)
    }
  }

  private fun sortCategories(categories: List<CategoryAdapter>): List<CategoryAdapter> {
    val mainCategory = categories.findLast { it.slug == "saviezvousque" }
    val mutableCategories = categories.toMutableList()
    mutableCategories.sortBy { it.name }
    mainCategory?.let {
      mutableCategories.remove(it)
      mutableCategories.add(0, it)
    }
    return mutableCategories
  }

  /**
   * Creates the given [category] if it does not exists.
   *
   * @param category value for category.
   * @param displayOrder display order for the category.
   */
  fun createCategory(category: Category, displayOrder: Int = 0) {
    if (!categoryExists(category.id)) {
      categoryQueries.createCategory(category.id, category.numberOfItems, category.name,
          category.slug, displayOrder)
    }
  }

  /**
   * Updates the [Category] with the given [categoryId].
   *
   * @param categoryId id of the [Category] to update.
   * @param newCategory new value for the category.
   * @throws CategoryIdNotFoundException if no [Category] with the given [categoryId] has been found.
   */
  @Throws(CategoryIdNotFoundException::class)
  fun updateCategory(categoryId: Int, newCategory: Category) {
    if (categoryExists(categoryId)) {
      updateCategoryItem(categoryId, newCategory)
    } else {
      throw CategoryIdNotFoundException(categoryId)
    }
  }

  private fun updateCategoryItem(categoryId: Int, category: Category) {
    categoryQueries.updateCategoryWithId(category.name, category.slug, category.numberOfItems,
        categoryId)
  }

  /** Deletes all categories. */
  fun deleteCategories() {
    categoryQueries.deleteCategories()
  }

  fun categoryExists(category: Category): Boolean = categoryExists(category.id)

  fun categoryExists(categoryId: Int): Boolean = categoryQueries.countCategoryWithId(
      categoryId).executeAsOne() > 0

  private fun List<CategoryAdapter>.toCategories(): List<Category> = mapIndexed { index, category ->
    Category.Impl(category.id, category.count, category.name, category.slug, index)
  }
}