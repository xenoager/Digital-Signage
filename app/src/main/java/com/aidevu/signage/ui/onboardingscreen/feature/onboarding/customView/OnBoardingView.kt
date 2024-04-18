package com.aidevu.signage.ui.onboardingscreen.feature.onboarding.customView

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.aidevu.signage.databinding.OnboardingViewBinding
import com.aidevu.signage.ui.MainActivity
import com.aidevu.signage.ui.onboardingscreen.core.setParallaxTransformation
import com.aidevu.signage.ui.onboardingscreen.domain.OnBoardingPrefManager
import com.aidevu.signage.ui.onboardingscreen.feature.onboarding.OnBoardingPagerAdapter
import com.aidevu.signage.ui.onboardingscreen.feature.onboarding.entity.OnBoardingPage
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class OnBoardingView @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val numberOfPages by lazy { OnBoardingPage.values().size }
    private val prefManager: OnBoardingPrefManager
    private var activity: Activity
    private var mInterstitialAd: InterstitialAd? = null

    init {
        val binding = OnboardingViewBinding.inflate(LayoutInflater.from(context), this, true)
        with(binding) {
            setUpSlider()
            addingButtonsClickListeners()
            activity = root.context as Activity
            prefManager = OnBoardingPrefManager(root.context)
            setupInterstitialAd()
        }

    }

    private fun OnboardingViewBinding.setUpSlider() {
        with(slider) {
            adapter = OnBoardingPagerAdapter()

            setPageTransformer { page, position ->
                setParallaxTransformation(page, position)
            }

            addSlideChangeListener()

            val wormDotsIndicator = pageIndicator
            wormDotsIndicator.attachTo(this)

        }
    }


    private fun OnboardingViewBinding.addSlideChangeListener() {

        slider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (numberOfPages > 1) {
                    val newProgress = (position + positionOffset) / (numberOfPages - 1)
                    onboardingRoot.progress = newProgress
                }
            }
        })
    }

    private fun OnboardingViewBinding.addingButtonsClickListeners() {
        nextBtn.setOnClickListener { navigateToNextSlide(slider) }
        skipBtn.setOnClickListener {
            setFirstTimeLaunchToFalse()
        }
        startBtn.setOnClickListener {
            setFirstTimeLaunchToFalse()
        }
    }

    private fun setFirstTimeLaunchToFalse() {
        prefManager.isFirstTimeLaunch = false
        Log.d("kts", "setFirstTimeLaunchToFalse")
        if (mInterstitialAd != null) {
            activity.startActivity(Intent(activity, MainActivity::class.java))
            mInterstitialAd?.show(activity)
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() { com.aidevu.signage.utils.Log.d("Ad was clicked.") }
                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    com.aidevu.signage.utils.Log.d("Ad dismissed fullscreen content.")
                    start()
                }
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    com.aidevu.signage.utils.Log.e("Ad failed to show fullscreen content.")
                    mInterstitialAd = null
                }
                override fun onAdImpression() { com.aidevu.signage.utils.Log.d("Ad recorded an impression.") }
                override fun onAdShowedFullScreenContent() { com.aidevu.signage.utils.Log.d("Ad showed fullscreen content.") }
            }
        } else {
            start()
        }
    }

    private fun setupInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(activity,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    com.aidevu.signage.utils.Log.d("DEBUG: " + adError?.message.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    com.aidevu.signage.utils.Log.d("Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun start() {
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }

    private fun navigateToNextSlide(slider: ViewPager2?) {
        val nextSlidePos: Int = slider?.currentItem?.plus(1) ?: 0
        slider?.setCurrentItem(nextSlidePos, true)
    }
}