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

/** Displays a *single* [FeedItem]. */
class PostView : ConstraintLayout {

  companion object {
    private const val SHARED_IMAGE_NAME = "pic_to_share.jpg"
  }

  private lateinit var imageView: AppCompatImageView
  private lateinit var shareImageButton: AppCompatButton

  private var imageUrl: String = ""

  /** URL of the post's image. */
  var postImageUrl: String
    get() = imageUrl
    set(value) {
      imageUrl = value
      //FIXME: fix error below
//      java.lang.IllegalArgumentException: Path must not be empty.
//      at com.squareup.picasso.Picasso.load(Picasso.java:332)
//      at com.ouattararomuald.saviezvousque.customviews.PostView.setPostImageUrl(PostView.kt:42)
//      at com.ouattararomuald.saviezvousque.databinding.PostItemViewBindingImpl.executeBindings(PostItemViewBindingImpl.java:119)
//      at androidx.databinding.ViewDataBinding.executeBindingsInternal(ViewDataBinding.java:473)
//      at androidx.databinding.ViewDataBinding.executePendingBindings(ViewDataBinding.java:445)
//      at com.ouattararomuald.saviezvousque.posts.PostViewHolder.bind(PostViewHolder.kt:18)
//      at com.ouattararomuald.saviezvousque.posts.archives.PagedPostAdapter.onBindViewHolder(PagedPostAdapter.kt:26)
//      at com.ouattararomuald.saviezvousque.posts.archives.PagedPostAdapter.onBindViewHolder(PagedPostAdapter.kt:12)
//      at androidx.recyclerview.widget.RecyclerView$Adapter.onBindViewHolder(RecyclerView.java:6979)
//      at androidx.recyclerview.widget.RecyclerView$Adapter.bindViewHolder(RecyclerView.java:7021)
//      at androidx.recyclerview.widget.RecyclerView$Recycler.tryBindViewHolderByDeadline(RecyclerView.java:5938)
//      at androidx.recyclerview.widget.RecyclerView$Recycler.tryGetViewHolderForPositionByDeadline(RecyclerView.java:6205)
//      at androidx.recyclerview.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:6044)
//      at androidx.recyclerview.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:6040)
//      at androidx.recyclerview.widget.LinearLayoutManager$LayoutState.next(LinearLayoutManager.java:2303)
//      at androidx.recyclerview.widget.LinearLayoutManager.layoutChunk(LinearLayoutManager.java:1627)
//      at androidx.recyclerview.widget.LinearLayoutManager.fill(LinearLayoutManager.java:1587)
//      at androidx.recyclerview.widget.LinearLayoutManager.scrollBy(LinearLayoutManager.java:1391)
//      at androidx.recyclerview.widget.LinearLayoutManager.scrollVerticallyBy(LinearLayoutManager.java:1128)
//      at androidx.recyclerview.widget.RecyclerView.scrollStep(RecyclerView.java:1847)
//      at androidx.recyclerview.widget.RecyclerView$ViewFlinger.run(RecyclerView.java:5229)
//      at android.view.Choreographer$CallbackRecord.run(Choreographer.java:1172)
//      at android.view.Choreographer.doCallbacks(Choreographer.java:984)
//      at android.view.Choreographer.doFrame(Choreographer.java:806)
//      at android.view.Choreographer$FrameDisplayEventReceiver.run(Choreographer.java:1158)
//      at android.os.Handler.handleCallback(Handler.java:873)
//      at android.os.Handler.dispatchMessage(Handler.java:99)
//      at android.os.Looper.loop(Looper.java:193)
//      at android.app.ActivityThread.main(ActivityThread.java:6863)
//      at java.lang.reflect.Method.invoke(Native Method)
//      at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:537)
//      at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
      Picasso.get().load(imageUrl)
          .placeholder(R.drawable.ic_image)
          .error(R.drawable.ic_broken_image)
          .into(imageView)
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
    val a = context.obtainStyledAttributes(attrs, R.styleable.PostView, defStyle, 0)

    if (a.hasValue(R.styleable.PostView_postImageUrl)) {
      postImageUrl = a.getString(R.styleable.PostView_postImageUrl) ?: ""
    }

    if (a.hasValue(R.styleable.PostView_postImageUrl)) {
      postTitle = a.getString(R.styleable.PostView_postTitle) ?: ""
    }

    a.recycle()
  }

  private fun launchSharingDialog() {
    // Extract Bitmap from ImageView drawable
    val drawable = imageView.drawable
    var bmp: Bitmap? = null
    if (drawable is BitmapDrawable) {
      bmp = (imageView.drawable as BitmapDrawable).bitmap
    } else {
      return
    }

    SingleFromCallable {
      getLocalBitmapUri(bmp)
    }.subscribeOn(Schedulers.io())
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
  private fun getLocalBitmapUri(bmp: Bitmap?): Uri? {
    // Store image to default external storage directory
    var bmpUri: Uri? = null
    try {
      val file = File(
          context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
          SHARED_IMAGE_NAME
      )
      val out = FileOutputStream(file)
      bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
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
}
