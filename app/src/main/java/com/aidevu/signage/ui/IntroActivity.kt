package com.aidevu.signage.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aidevu.signage.databinding.OnboardingActivityBinding
import com.aidevu.signage.ui.onboardingscreen.domain.OnBoardingPrefManager

class IntroActivity :  AppCompatActivity() {

    private lateinit var prefManager: OnBoardingPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = OnboardingActivityBinding.inflate(layoutInflater)
        prefManager = OnBoardingPrefManager(binding.root.context)

        Log.d("kts", "prefManager.isFirstTimeLaunch : " + prefManager.isFirstTimeLaunch)
        setContentView(binding.root)


        if(prefManager.isFirstTimeLaunch) {
            binding.container.visibility = View.VISIBLE
            binding.mainView.visibility = View.GONE
        }else{
            Log.d("kts", "화면 띄운다.")
            binding.container.visibility = View.GONE
            binding.mainView.visibility = View.VISIBLE
            startActivity(Intent(this@IntroActivity, SplashActivity::class.java))
            finish()
        }
    }
}