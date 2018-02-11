package com.ouattararomuald.saviezvousque.downloaders

import java.io.File
import org.simpleframework.xml.core.Persister


class TestsUtil {
  companion object {
    const val FEED_FILE_NAME = "/feed.xml"

    @JvmStatic
    fun loadFileFromResources(filename: String): File {
      val url = TestsUtil::class.java.getResource(filename)
      return File(url.toURI())
    }

    @JvmStatic
    fun deserializeXmlFromFile(source: File): Rss? {
      val serializer = Persister()
      return serializer.read(Rss::class.java, source)
    }
  }
}