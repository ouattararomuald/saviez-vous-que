package com.ouattararomuald.saviezvousque.db.daos

import com.ouattararomuald.saviezvousque.db.Category
import com.ouattararomuald.saviezvousque.db.CategoryQueries
import com.ouattararomuald.saviezvousque.db.Database
import com.ouattararomuald.saviezvousque.db.Post
import com.ouattararomuald.saviezvousque.db.adapters.LocalDateTimeAdapter
import com.ouattararomuald.saviezvousque.db.exceptions.CategoryIdNotFoundException
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import com.ouattararomuald.saviezvousque.common.Category as CategoryAdapter

class CategoryDaoTest {

  private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  private val dateAdapter = LocalDateTimeAdapter()

  private lateinit var categoryQueries: CategoryQueries
  private lateinit var categoryDao: CategoryDao

  @Before
  fun setUp() {
    Database.Schema.create(driver)
    val database = Database(driver, Post.Adapter(dateAdapter, dateAdapter))

    categoryQueries = spy(database.categoryQueries)
    categoryDao = CategoryDao(categoryQueries)
  }

  @After
  fun tearDown() {
  }

  @Test
  fun `verify calls for getCategories()`() {
    categoryDao.getCategories()

    verify(categoryQueries, times(1)).categoryIdAndName()
  }

  @Test
  fun `create new category should success`() {
    val category = createCategory(id = 1)

    categoryDao.createCategory(category)

    verify(categoryQueries, times(1)).countCategoryWithId(categoryId = category.id)
    verify(categoryQueries, times(1)).createCategory(category.id, category.numberOfItems,
        category.name, category.slug, category.displayOrder)
    verify(categoryQueries, never()).updateCategoryWithId(anyString(), anyString(), anyInt(),
        anyInt())
  }

  @Test
  fun `create category with an existing one does nothing`() {
    val category = createCategory(id = 1)

    categoryDao.createCategory(category)
    categoryDao.createCategory(category)

    verify(categoryQueries, times(2)).countCategoryWithId(categoryId = category.id)
    verify(categoryQueries, times(1)).createCategory(category.id, category.numberOfItems,
        category.name, category.slug, displayOrder = 0)
    verify(categoryQueries, never()).updateCategoryWithId(anyString(), anyString(), anyInt(),
        anyInt())
  }

  @Test
  fun `createCategories() should delegate to createCategory()`() {
    val categories = listOf(createCategory(id = 1), createCategory(id = 2), createCategory(id = 3))
    val categoryAdapters = categories.map { it.toAdapter() }

    val spyCategoryDao = spy(categoryDao)
    spyCategoryDao.createCategories(categoryAdapters)

    verify(spyCategoryDao, times(1)).createCategories(categoryAdapters)
    categoryAdapters.toCategories().forEach { category ->
      verify(spyCategoryDao, times(1)).createCategory(category = category,
          displayOrder = category.displayOrder)
    }
  }

  @Test
  fun `update existing category should success`() {
    val category = createCategory(id = 1)

    categoryDao.createCategory(category)
    categoryDao.updateCategory(category.id, category)

    verify(categoryQueries, times(2)).countCategoryWithId(categoryId = category.id)
    verify(categoryQueries, times(1)).updateCategoryWithId(category.name, category.slug,
        category.numberOfItems, category.id)
  }

  @Test
  fun `categoryExists(category) should delegate to categoryExists(categoryId)`() {
    val category = createCategory(id = 2)

    val spyCategoryDao = spy(categoryDao)

    spyCategoryDao.categoryExists(category)

    verify(categoryQueries, times(1)).countCategoryWithId(categoryId = category.id)
    verify(spyCategoryDao, times(1)).categoryExists(category.id)
  }

  @Test(expected = CategoryIdNotFoundException::class)
  fun `update on not existing category should fail`() {
    val category = createCategory(id = 1)

    categoryDao.updateCategory(category.id, category)
  }

  @Test
  fun `deleteCategories() should success`() {
    categoryDao.deleteCategories()

    verify(categoryQueries, never()).countCategoryWithId(categoryId = anyInt())
    verify(categoryQueries, times(1)).deleteCategories()
  }

  private fun createCategories(quantity: Int): List<Category> {
    val categories = mutableListOf<Category>()
    repeat(quantity) {
      val category = createCategory(id = it)
      categories.add(category)
    }

    return categories
  }

  private fun createCategory(id: Int): Category {
    return Category(
      id = id,
      numberOfItems = id * 10,
      name = "Category $id",
      slug = "category-slug-$id",
      displayOrder = 0
    )
  }

  private fun Category.toAdapter(): CategoryAdapter {
    return CategoryAdapter(this.id, this.numberOfItems, this.name, this.slug)
  }

  private fun List<CategoryAdapter>.toCategories(): List<Category> = mapIndexed { index, category ->
    Category(category.id, category.count, category.name, category.slug, index)
  }
}