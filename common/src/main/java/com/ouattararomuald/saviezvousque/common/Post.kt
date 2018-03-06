package com.ouattararomuald.saviezvousque.common

import com.google.gson.annotations.SerializedName
import java.util.*

data class Post(
    val id: Int,

    @SerializedName(value = "date_gmt")
    val publicationDateUtc: String,

    @SerializedName(value = "modified_gmt")
    val lastUpdateUtc: String,

    val title: Title,

    val content: Content
) {
  @Suppress("ConvertTwoComparisonsToRangeCheck")
  fun getImageUrl(): String? {
    val src = "src=\""
    val imageSrcIndex = content.value.indexOf(src)
    val imageLinkStartIndex = imageSrcIndex + src.length
    if (imageLinkStartIndex > 0) {
      val imageLinkEndIndex = content.value.indexOf("\"", imageLinkStartIndex)
      if (imageLinkStartIndex > 0 && imageLinkEndIndex > imageLinkStartIndex) {
        return content.value.substring(imageLinkStartIndex, imageLinkEndIndex)
      }
    }

    return null
  }
}