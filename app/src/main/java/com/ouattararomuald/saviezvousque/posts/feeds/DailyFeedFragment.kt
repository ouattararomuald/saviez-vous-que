package com.ouattararomuald.saviezvousque.posts.feeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ouattararomuald.saviezvousque.R
import com.ouattararomuald.saviezvousque.common.Post
import com.ouattararomuald.saviezvousque.posts.EmptyPostItem
import com.ouattararomuald.saviezvousque.posts.PostItem
import com.ouattararomuald.saviezvousque.posts.SharedViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_daily_feed.postsRecyclerView

class DailyFeedFragment : Fragment() {

  private lateinit var model: SharedViewModel

  private var postSection = Section().apply {
    setPlaceholder(EmptyPostItem())
  }
  private val groupAdapter = GroupAdapter<ViewHolder>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_daily_feed, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    model = activity?.run {
      ViewModelProviders.of(this).get(SharedViewModel::class.java)
    } ?: throw IllegalStateException("Invalid Activity")

    groupAdapter.add(postSection)

    postsRecyclerView.apply {
      layoutManager = LinearLayoutManager(context).apply {
        reverseLayout = true
      }
      setHasFixedSize(true)
      adapter = groupAdapter
    }

    model.posts.observe(this, Observer<List<Post>> { posts ->
      postSection.update(posts.map { post -> PostItem(post) })
    })
  }

  override fun onPause() {
    super.onPause()
    groupAdapter.remove(postSection)
  }
}
