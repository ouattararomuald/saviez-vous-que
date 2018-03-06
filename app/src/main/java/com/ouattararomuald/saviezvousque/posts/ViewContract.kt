package com.ouattararomuald.saviezvousque.posts

import com.ouattararomuald.saviezvousque.common.Category
import com.ouattararomuald.saviezvousque.common.Post

interface ViewContract {

  fun onCategoriesDownloaded(categories: List<Category>)

  fun onPostsDownloaded(posts: List<Post>)

  fun notifyDatasetChanged()
}