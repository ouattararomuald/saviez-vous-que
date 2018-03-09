package com.ouattararomuald.saviezvousque.common

import com.google.gson.annotations.SerializedName

data class Title(
    @SerializedName(value = "rendered")
    val value: String
)
