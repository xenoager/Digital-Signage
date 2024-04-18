package com.aidevu.signage.ui.custom.loopingviewpager

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aidevu.signage.databinding.ListVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class VideoAdapter(
    var context: Context,
    var video: ArrayList<Video>,
    private var videoPreparedListener: OnVideoPreparedListener
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    private var listSlider = arrayListOf<Video>()
    class VideoViewHolder(
        val binding: ListVideoBinding,
        var context: Context,
        var videoPreparedListener: OnVideoPreparedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var exoPlayer: ExoPlayer
        private lateinit var mediaSource: MediaSource

        fun setVideoPath(filePath: String, duration: Int, fileType: String) {
            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
//                    Toast.makeText(context, "Can't play this video", Toast.LENGTH_SHORT).show()
                    Log.d("kts", "onPlayerError: $filePath")
                    binding.pbLoading.visibility = View.GONE
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    binding.pbLoading.visibility = View.GONE
                }
            })

            binding.playerView.player = exoPlayer

            exoPlayer.seekTo(0)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            val dataSourceFactory = DefaultDataSource.Factory(context)

            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(filePath)))

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            if (absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition, duration, fileType, binding.imageView, binding.playerView, filePath))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = ListVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideoViewHolder(view, context, videoPreparedListener)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val actualPosition = position % listSlider.size
        val model = listSlider[actualPosition]

        holder.binding.tvTitle.text = model.title
        holder.setVideoPath(model.filePath, model.duration, model.fileType)
    }

    override fun getItemCount(): Int {
        return listSlider.size
    }

    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }

    fun setItems(items: List<Video>) {
        listSlider.clear()
        listSlider.addAll(items)
        if (listSlider.isNotEmpty()) {
            listSlider = (listOf(listSlider.last()) + listSlider + listOf(listSlider.first())) as ArrayList<Video>
            notifyDataSetChanged()
        }
    }
}