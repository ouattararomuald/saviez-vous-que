package com.ouattararomuald.saviezvousque.posts

import com.ouattararomuald.saviezvousque.db.PostWithCategory

interface HomeContract {

  interface Presenter {
    /** Notifies the presenter that the view will be destroyed. */
    fun onDestroy()

    /** Asks the presenter to refresh its dara. */
    fun refreshData()

    /**
     * Gets the posts that belong to the category with given [categoryId].
     *
     * @param categoryId id of the category whose you want posts.
     * @return list of posts.
     */
    fun getPostsByCategory(categoryId: Int): List<PostWithCategory>
  }

  interface View {
    fun showProgressBar()

    fun hideProgressBar()
  }
}
