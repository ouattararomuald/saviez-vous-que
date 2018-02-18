package com.ouattararomuald.saviezvousque.common

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PostTest {

  @Test
  fun validContentShouldReturnImageUrl() {
    val post = Post(
        id = 1,
        publicationDateUtc = "2018-02-17T20:20:20",
        lastUpdateUtc = "2018-02-17T20:20:20",
        content = Content(value = "<p><img src=\\\"https:\\/\\/i.pinimg.com\\/564x\\/51\\/06\\/51\\/510651569a57a6f53579ae68019d28b3.jpg\\\" width=\\\"564\\\" height=\\\"564\\\" class=\\\"alignnone size-medium\\\" \\/><\\/p>\\n")
    )

    assertThat(post.getImageUrl()).isNotNull()
    assertThat(post.getImageUrl()).isEqualTo("https:\\/\\/i.pinimg.com\\/564x\\/51\\/06\\/51\\/510651569a57a6f53579ae68019d28b3.jpg\\")
  }

  @Test
  fun invalidContentShouldReturnNullImageUrl() {
    val post = Post(
        id = 1,
        publicationDateUtc = "2018-02-17T20:20:20",
        lastUpdateUtc = "2018-02-17T20:20:20",
        content = Content(value = "<p>")
    )

    assertThat(post.getImageUrl()).isNull()
  }
}