package com.ouattararomuald.saviezvousque.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Title(
  @SerializedName(value = "rendered")
  val value: String
) : Parcelable
