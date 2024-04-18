package com.aidevu.signage.ui.onboardingscreen.feature.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aidevu.signage.databinding.OnboardingActivityBinding

class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      val  binding = OnboardingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}