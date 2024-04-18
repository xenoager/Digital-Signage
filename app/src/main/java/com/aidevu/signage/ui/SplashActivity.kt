package com.aidevu.signage.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.aidevu.signage.databinding.ActivitySplashBinding
import com.aidevu.signage.utils.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class SplashActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                binding.motionLayout.visibility = View.GONE
                binding.mainImageView.visibility = View.VISIBLE
                binding.startBtn.visibility = View.VISIBLE
                Log.d("onTransitionCompleted")
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) { }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) { }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) { }
        })

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                binding.motionLayout.visibility = View.GONE
                binding.mainImageView.visibility = View.VISIBLE
                binding.startBtn.visibility = View.VISIBLE
                Log.d("onTransitionCompleted 안되면 처리하는 예외코드")
            } catch (e: Exception) {
            }
        }, 5500)



        setupInterstitialAd()

        binding.startBtn.setOnClickListener {
            if (mInterstitialAd != null) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                mInterstitialAd?.show(this@SplashActivity)
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() { Log.d("Ad was clicked.") }
                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        Log.d("Ad dismissed fullscreen content.")
                        start()
                        mInterstitialAd = null
                        setupInterstitialAd()
                    }
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e("Ad failed to show fullscreen content.")
                        mInterstitialAd = null
                    }
                    override fun onAdImpression() { Log.d("Ad recorded an impression.") }
                    override fun onAdShowedFullScreenContent() { Log.d("Ad showed fullscreen content.") }
                }
            } else {
                start()
            }
        }
    }

    private fun start() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()


//        Handler(Looper.getMainLooper()).postDelayed({
//            try {
//
//            } catch (e: Exception) {
//            }
//        }, 1000)
    }

    private fun setupInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("DEBUG: " + adError?.message.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
    }
}