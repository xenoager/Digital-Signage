package com.aidevu.signage.ui.custom.loopingviewpager

import android.widget.ImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView

class ExoPlayerItem(
    var exoPlayer: ExoPlayer,
    var position: Int,
    var duration: Int,
    var playType: String,
    var imageView: ImageView,
    var videoView: StyledPlayerView,
    var filePath: String
)