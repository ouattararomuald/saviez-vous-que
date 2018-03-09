package com.ouattararomuald.saviezvousque.common

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName(value = "rendered")
    val value: String
)
