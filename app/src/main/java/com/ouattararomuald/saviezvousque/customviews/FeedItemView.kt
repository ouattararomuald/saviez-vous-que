package com.ouattararomuald.saviezvousque.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.db.FeedItem
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.single.SingleFromCallable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/** Custom View for [FeedItem]. */
class FeedItemView : ConstraintLayout {

  private lateinit var imageView: AppCompatImageView
  private lateinit var shareImageButton: AppCompatButton

  private var imageUrl: String = ""

  /** URL of the post's image. */
  var postImageUrl: String
    get() = imageUrl
    set(value) {
      imageUrl = value
      if (imageUrl.isNotEmpty()) {
        Picasso.get().load(imageUrl)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_broken_image)
            .into(imageView)
      }
    }

  /** Title of the post. */
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
    shareImageButton = findViewById(R.id.share_image_button)

    shareImageButton.setOnClickListener { launchSharingDialog() }

    // Load attributes
    val a = context.obtainStyledAttributes(attrs, R.styleable.FeedItemView, defStyle, 0)

    if (a.hasValue(R.styleable.FeedItemView_postImageUrl)) {
      postImageUrl = a.getString(R.styleable.FeedItemView_postImageUrl) ?: ""
    }

    if (a.hasValue(R.styleable.FeedItemView_postImageUrl)) {
      postTitle = a.getString(R.styleable.FeedItemView_postTitle) ?: ""
    }

    a.recycle()
  }

  private fun launchSharingDialog() {
    // Extract Bitmap from ImageView drawable
    val drawable = imageView.drawable
    val bmp: Bitmap?

    if (drawable is BitmapDrawable) {
      bmp = (imageView.drawable as BitmapDrawable).bitmap
    } else {
      return
    }

    SingleFromCallable {
      getBitmapUri(bmp)
    }.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .doAfterSuccess {
          it?.let {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, it)
            shareIntent.type = "image/*"
            context.startActivity(Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_image)
            ))
          }
        }.subscribe()
  }

  @SuppressLint("WrongThread")
  private fun getBitmapUri(bmp: Bitmap?): Uri? {
    // Store image to default external storage directory
    var bmpUri: Uri? = null

    try {
      val file = File(
          context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
          SHARED_IMAGE_NAME
      )
      val out = FileOutputStream(file)
      bmp!!.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
      out.close()
      bmpUri = FileProvider.getUriForFile(
          context,
          "com.ouattararomuald.saviezvousque.fileprovider",
          file
      )
    } catch (e: IOException) {
      e.printStackTrace()
    }

    return bmpUri
  }

  companion object {
    private const val SHARED_IMAGE_NAME = "pic_to_share.jpg"
    private const val COMPRESSION_QUALITY = 100
  }
}
