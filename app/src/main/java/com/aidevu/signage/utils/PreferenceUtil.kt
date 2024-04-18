package com.aidevu.signage.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {

    private val prefName = "PREF_AD_ORDER"
    private val adOrder = "adOrder"

    private val image = "image"
    private val video = "video"
    private val fullMode = "fullMode"
    private val introCheck = "introCheck"

    private val _prefs: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val prefs get() = _prefs!!

    fun getAdOrder(): String {
        return prefs.getString(adOrder, "").toString()
    }

    fun setAdOrder(str: String) {
        prefs.edit().putString(adOrder, str).commit()
    }

    fun clearAdOrder() {
        prefs.edit().remove(adOrder).commit()
    }

    fun getImage(): String {
        return prefs.getString(image, "").toString()
    }

    fun setImage(str: String) {
        prefs.edit().putString(image, str).commit()
    }

    fun getFullMode(): Boolean {
        return prefs.getBoolean(fullMode, false)
    }

    fun setFullMode(flag: Boolean) {
        prefs.edit().putBoolean(fullMode, flag).commit()
    }

    fun clearImage() {
        prefs.edit().remove(image).commit()
    }

    fun getVideo(): String {
        return prefs.getString(video, "").toString()
    }

    fun setVideo(str: String) {
        prefs.edit().putString(video, str).commit()
    }

    fun clearVideo() {
        prefs.edit().remove(video).commit()
    }

    fun getIntroCheck(): Boolean {
        return prefs.getBoolean(introCheck, false)
    }

    fun setIntroCheck(flag: Boolean) {
        prefs.edit().putBoolean(introCheck, flag).commit()
    }

    fun clearIntroCheck() {
        prefs.edit().remove(introCheck).commit()
    }
}