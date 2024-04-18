package com.aidevu.signage.ui.video

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.aidevu.signage.R
import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.databinding.ActivityLoopingViewBinding
import com.aidevu.signage.repository.Repository
import com.aidevu.signage.ui.base.BaseActivity
import com.aidevu.signage.ui.custom.loopingviewpager.ExoPlayerItem
import com.aidevu.signage.ui.custom.loopingviewpager.Video
import com.aidevu.signage.ui.custom.loopingviewpager.VideoAdapter
import com.aidevu.signage.viewmodel.video.LoopingViewModel
import com.aidevu.signage.utils.Log
import com.aidevu.signage.utils.Utils
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class LoopingViewActivity : BaseActivity<ActivityLoopingViewBinding, LoopingViewModel>() {

    @Inject
    lateinit var repository: Repository

    private lateinit var adapter: VideoAdapter
    private val videos = ArrayList<Video>()
    private val exoPlayerItems = ArrayList<ExoPlayerItem>()

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val rightScrollHandler = Handler(Looper.getMainLooper())

    private var autoScrollDelay = 5000
    private var autoScrollRunnable: Runnable? = null
    private var isScrollingLeft = false
    private var isScrollingRight = false
    private var isAutoScrolling = false
    private var sliderAdapter: VideoAdapter? = null
    private var first = false

    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var list: List<Ad>
    private lateinit var activity: Activity

    override fun createViewModel(): LoopingViewModel {
        return ViewModelProvider(this)[LoopingViewModel::class.java]
    }

    override fun createViewBinding(layoutInflater: LayoutInflater): ActivityLoopingViewBinding {
        return ActivityLoopingViewBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBar()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity = this

        val a = scope.async {
            list = viewModel.getOrderDescList().sortedBy {
                it.getOrder()
            }

            for (i in list.indices) {
                // Log.d("getDuration : ${list[i].getDuration()}, getOrder : ${list[i].getOrder()}, getFileName : ${list[i].getFileName()}, getFilePath : ${list[i].getFilePath()}")
                videos.add(
                    Video(
                        list[i].getFileName(),
                        list[i].getFilePath(),
                        list[i].getDuration() * 1000,
                        list[i].getFileType(),
                    )
                )
            }

            if (list != null && list.isNotEmpty()) {
                init()
            } else {
                Utils.showToast(activity, activity.getString(R.string.str_no_data))
                Log.d("먼저 광고를 추가해 주세요.")
                finish()
            }
        }
    }

    var firstCheck = false;

    private fun init() {
        adapter = VideoAdapter(this, videos, object : VideoAdapter.OnVideoPreparedListener {
            override fun onVideoPrepared(exoPlayerItem: ExoPlayerItem) {
                exoPlayerItems.add(exoPlayerItem)
            }
        })
        adapter.setItems(videos)
        binding.homeViewPager.isUserInputEnabled = false;
        binding.homeViewPager.adapter = adapter
        sliderAdapter = adapter

        binding.homeViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                val previousIndex = exoPlayerItems.indexOfFirst {
                    it.exoPlayer.isPlaying
                }

                android.util.Log.d("kts", "onPageSelected : $position")
                android.util.Log.d("kts", "onPageSelected previousIndex : $previousIndex")
                if (previousIndex != -1) {
                    val player = exoPlayerItems[previousIndex].exoPlayer
                    player.seekTo(0)
                    player.pause()
                    player.playWhenReady = false
                    android.util.Log.e("kts", "onPageSelected 33333")
                }
                val newIndex = exoPlayerItems.indexOfFirst { it.position == position }
                android.util.Log.d("kts", "onPageSelected newIndex : $newIndex")
                android.util.Log.d("kts", "onPageSelected -----------------------------")
                if (newIndex != -1) {
                    autoScrollDelay = exoPlayerItems[newIndex].duration
                    if(exoPlayerItems[newIndex].playType == "video") {
                        exoPlayerItems[newIndex].imageView.visibility = View.GONE
                        exoPlayerItems[newIndex].videoView.visibility = View.VISIBLE
                        val player = exoPlayerItems[newIndex].exoPlayer
                        player.playWhenReady = true
                        player.play()
                        android.util.Log.e("kts", "onPageSelected 11111")
                    }else {
                        android.util.Log.e("kts", "onPageSelected 22222")
                        exoPlayerItems[newIndex].imageView.visibility = View.VISIBLE
                        exoPlayerItems[newIndex].videoView.visibility = View.GONE

                        if(prefs.getFullMode()) {
                            Glide
                                .with(activity)
                                .asBitmap()
                                .centerCrop() // 가운데 풀로 맞춤
                                .load(Uri.fromFile(File(exoPlayerItems[newIndex].filePath)))
                                .into(exoPlayerItems[newIndex].imageView);
                        }else{
                            Glide
                                .with(activity)
                                .asBitmap()
                                .fitCenter()  // 가운데 맞춤
                                //.centerCrop() // 가운데 풀로 맞춤
                                .load(Uri.fromFile(File(exoPlayerItems[newIndex].filePath)))
                                .into(exoPlayerItems[newIndex].imageView);
                        }
                    }
                }
            }
        })

//        adjustSpeed()
        scrollListener()
        rightScrollHandler.postDelayed({
            startAutoScrollRight()
        }, 2000)
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------------------

    override fun observeData() {

    }

    override fun onPause() {
        super.onPause()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.homeViewPager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()

        val index = exoPlayerItems.indexOfFirst { it.position == binding.homeViewPager.currentItem }
        if (index != -1) {
            val player = exoPlayerItems[index].exoPlayer
            player.playWhenReady = true
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayerItems.isNotEmpty()) {
            for (item in exoPlayerItems) {
                val player = item.exoPlayer
                player.stop()
                player.clearMediaItems()
            }
        }
        stopAutoScroll()
    }

    private fun scrollListener() {
        with(binding) {
            val recyclerView = binding.homeViewPager.getChildAt(0) as RecyclerView
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val itemCount = sliderAdapter!!.itemCount
            val actualItemCount = itemCount - 2 // Number of actual items , remove first and last imaginal duplicates

            homeViewPager.adapter = sliderAdapter
            //homeViewPager.setPageTransformer(null)
            binding.homeViewPager.offscreenPageLimit = actualItemCount

            binding.homeViewPager.setCurrentItem(0, false)
            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val firstItemVisible = layoutManager.findFirstVisibleItemPosition()
                    val lastItemVisible = layoutManager.findLastVisibleItemPosition()

                    if(!first) {
                        binding.homeViewPager.setCurrentItem(1, false)
                        first = true
                    }
                    if (firstItemVisible == itemCount - 1 && dx > 0) {
                        //Log.d("Array","firstItemVisible:$firstItemVisible")
                        Log.d("처음으로 돌아옴. 처음 값으로 셋팅")
                        binding.homeViewPager.setCurrentItem(1, false)
                    }
                    if (lastItemVisible == 0 && dx < 0) {
                        //Log.d("Array","lastItemVisible:$lastItemVisible")
                        binding.homeViewPager.setCurrentItem(itemCount - 2, false)
                        Log.d("여긴뭐지?")
                    }
                }
            }
            recyclerView.addOnScrollListener(scrollListener)
        }

    }

    private fun startAutoScrollRight() {
        stopAutoScroll()
        isAutoScrolling = true
        isScrollingRight = true
        isScrollingLeft = false
        autoScrollRunnable = Runnable {
            if(binding != null && binding.homeViewPager != null) {
                val currentItem = binding.homeViewPager.currentItem
                val itemCount = sliderAdapter?.itemCount ?: 0

                val nextItem = if (currentItem == itemCount - 1) 0 else currentItem + 1
                binding.homeViewPager.setCurrentItem(nextItem, true)
                autoScrollRunnable?.let {
                    autoScrollHandler.postDelayed(
                        it,
                        autoScrollDelay.toLong()
                    )
                }
            }
        }
        autoScrollHandler?.let {
            it.postDelayed(autoScrollRunnable!!, autoScrollDelay.toLong())
        }
    }

    private fun startAutoScrollLeft() {
        stopAutoScroll()
        isAutoScrolling = true
        isScrollingLeft = true
        isScrollingRight = false
        autoScrollRunnable = Runnable {
            val currentItem = binding.homeViewPager.currentItem
            val itemCount = sliderAdapter?.itemCount ?: 0

            val nextItem = if (currentItem == 0) itemCount - 1 else currentItem - 1
            binding.homeViewPager.setCurrentItem(nextItem, true)
            autoScrollRunnable?.let { autoScrollHandler.postDelayed(it, autoScrollDelay.toLong()) }
        }
        autoScrollHandler.postDelayed(autoScrollRunnable!!, autoScrollDelay.toLong())
    }

    private fun stopAutoScroll() {
        isAutoScrolling = false
        isScrollingRight = false
        isScrollingLeft = false
        autoScrollRunnable?.let {
            autoScrollHandler.removeCallbacks(it)
            autoScrollRunnable = null
        }

        if(autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
        }
        if(rightScrollHandler != null) {
            rightScrollHandler.removeCallbacksAndMessages(null);
        }
    }
}
