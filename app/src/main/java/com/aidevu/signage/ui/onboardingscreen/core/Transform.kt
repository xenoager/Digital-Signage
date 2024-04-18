package com.aidevu.signage.ui.onboardingscreen.core

import android.view.View
import android.widget.ImageView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.aidevu.signage.R

val pageCompositePageTransformer = CompositePageTransformer().apply {
    addTransformer(MarginPageTransformer(40))
    addTransformer { page, position ->
        val r = 1 - kotlin.math.abs(position)
        page.scaleY = 0.85f + r * 0.15f
    }
}

fun setParallaxTransformation(page: View, position: Float){
    page.apply {
        val parallaxView = this.findViewById<ImageView>(R.id.img)
        when {
            position < -1 ->
                alpha = 1f
            position <= 1 -> {
                parallaxView.translationX = -position * (width / 2) //Half the normal speed
            }
            else ->
                alpha = 1f
        }
    }
}