package com.ouattararomuald.saviezvousque.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
  val id: Int,

  val categoryId: Int,

  @SerializedName(value = "date_gmt")
  val publicationDateUtc: String,

  @SerializedName(value = "modified_gmt")
  val lastUpdateUtc: String,

  val title: Title,

  val content: Content
) : Parcelable {

  fun getImageUrl(): String {
    val src = "src=\""
    val imageSrcIndex = content.value.indexOf(src)
    val imageLinkStartIndex = imageSrcIndex + src.length
    if (imageLinkStartIndex > 0) {
      val imageLinkEndIndex = content.value.indexOf("\"", imageLinkStartIndex)
      if (imageLinkStartIndex in 1..(imageLinkEndIndex - 1)) {
        return content.value.substring(imageLinkStartIndex, imageLinkEndIndex)
      }
    }

    return ""
  }
}
