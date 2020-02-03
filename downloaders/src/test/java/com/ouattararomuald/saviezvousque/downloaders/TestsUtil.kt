package com.ouattararomuald.saviezvousque.downloaders

import java.io.File


class TestsUtil {
  companion object {
    const val CATEGORIES_JSON_FILE = "/categories.json"
    const val POSTS_JSON_FILE = "/posts.json"

    @JvmStatic
    fun loadFileFromResources(filename: String): File {
      val url = TestsUtil::class.java.getResource(filename)
      return File(url!!.toURI())
    }
  }
}