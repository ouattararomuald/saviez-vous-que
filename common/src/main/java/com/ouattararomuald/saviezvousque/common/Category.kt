package com.ouattararomuald.saviezvousque.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
  val id: Int,
  val count: Int,
  val name: String,
  val slug: String
) : Parcelable
