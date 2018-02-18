package com.ouattararomuald.saviezvousque.common

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,

    @SerializedName(value = "date_gmt")
    val publicationDateUtc: String,

    @SerializedName(value = "modified_gmt")
    val lastUpdateUtc: String,

    val content: Content
)