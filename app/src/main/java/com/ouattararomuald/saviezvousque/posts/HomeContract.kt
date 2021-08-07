package com.ouattararomuald.saviezvousque.posts

interface HomeContract {

  interface Presenter {

    /** Asks the presenter to refresh its dara. */
    fun refreshData()

    /**
     * Gets the posts that belong to the category with given [categoryId].
     *
     * @param categoryId id of the category whose you want posts.
     */
    fun categoryClicked(categoryId: Int)
  }

  interface View
}
