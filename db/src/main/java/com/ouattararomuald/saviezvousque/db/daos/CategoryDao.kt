package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.db.Category
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.CategoryQueries
import com.ouattararomuald.saviezvousque.db.exceptions.CategoryIdNotFoundException
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryDao @Inject constructor(private val categoryQueries: CategoryQueries) {

  fun getCategories(): Flow<List<CategoryIdAndName>> = categoryQueries.categoryIdAndName().asFlow().mapToList()

  /**
   * Creates all the given [categories].
   *
   * Existing categories won't be created.
   *
   * @param categories list of categories to create.
   */
  fun createCategories(categories: List<Category>) {
    categories.forEach { category -> createCategory(category) }
  }

  /**
   * Creates the given [category] if it does not exists.
   *
   * @param category value for category.
   */
  fun createCategory(category: Category) {
    if (!categoryExists(category.id)) {
      categoryQueries.createCategory(category.id, category.numberOfItems, category.name,
          category.slug)
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
}