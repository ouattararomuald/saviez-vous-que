package com.ouattararomuald.saviezvousque.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ouattararomuald.saviezvousque.common.Post

internal class SharedViewModel : ViewModel() {

  val categoryId = MutableLiveData<Int>()
  val posts = MutableLiveData<List<Post>>()
}
