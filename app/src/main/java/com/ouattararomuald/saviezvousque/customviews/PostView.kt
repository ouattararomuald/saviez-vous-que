package com.ouattararomuald.saviezvousque.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.facebook.drawee.view.SimpleDraweeView
import com.ouattararomuald.saviezvousque.R


/** Displays a post. */
class PostView : LinearLayout {

  private lateinit var imageView: SimpleDraweeView

  private var imageUrl: String = ""

  var postImageUrl: String
    get() = imageUrl
    set(value) {
      imageUrl = value
      imageView.setImageURI(imageUrl)
    }

  var postTitle: String = ""

  constructor(context: Context) : this(context, null)

  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
      : super(context, attrs, defStyle) {
    init(attrs, defStyle)
  }

  private fun init(attrs: AttributeSet?, defStyle: Int) {
    inflate(context, R.layout.post_card_view, this)
    imageView = findViewById(R.id.post_image_view)

    orientation = LinearLayout.VERTICAL

    // Load attributes
    val a = context.obtainStyledAttributes(attrs, R.styleable.PostView, defStyle, 0)

    postImageUrl = a.getString(R.styleable.PostView_postImageUrl)
    postTitle = a.getString(R.styleable.PostView_postTitle)

    a.recycle()
  }
}