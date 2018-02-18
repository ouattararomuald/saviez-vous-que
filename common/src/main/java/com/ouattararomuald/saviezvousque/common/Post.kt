package com.ouattararomuald.saviezvousque.common

import com.google.gson.annotations.SerializedName
import java.util.*

data class Post(
    val id: Int,

    @SerializedName(value = "date_gmt")
    val publicationDateUtc: String,

    @SerializedName(value = "modified_gmt")
    val lastUpdateUtc: String,

    val content: Content
) {
  fun getImageUrl(): String? {
    val tokenizer = StringTokenizer(content.value, "\"")
    if (tokenizer.countTokens() > 1) {
      tokenizer.nextToken()
      return tokenizer.nextToken()
    }
    return null
  }
}