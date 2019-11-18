package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.db.Category
import com.ouattararomuald.saviezvousque.db.CategoryIdAndName
import com.ouattararomuald.saviezvousque.db.CategoryQueries
import com.ouattararomuald.saviezvousque.db.exceptions.CategoryIdNotFoundException
import javax.inject.Inject

class CategoryDao @Inject constructor(private val categoryQueries: CategoryQueries) {

  fun getCategories(): List<CategoryIdAndName> = categoryQueries.categoryIdAndName().executeAsList()

  fun createCategories(categories: List<Category>) {
    categories.forEach { category -> createCategory(category) }
  }

  fun createCategory(category: Category) {
    if (!categoryExists(category.id)) {
      categoryQueries.createCategory(category.id, category.numberOfItems, category.name,
          category.slug)
    } else {
      updateCategory(category.id, category)
    }
  }

  fun updateCategory(categoryId: Int, newCategory: Category) {
    if (categoryExists(categoryId)) {
      categoryQueries.updateCategoryWithId(newCategory.name, newCategory.slug,
          newCategory.numberOfItems, categoryId)
    } else {
      throw CategoryIdNotFoundException(categoryId)
    }
  }

  private fun categoryExists(categoryId: Int): Boolean = categoryQueries.countCategoryWithId(categoryId).executeAsOne() > 0
}