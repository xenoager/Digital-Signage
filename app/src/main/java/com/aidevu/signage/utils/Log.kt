package com.aidevu.signage.utils

import com.aidevu.signage.BuildConfig

object Log {

    private const val tag: String = "kts";

    fun d(str: String) {
        if(BuildConfig.DEBUG)
            android.util.Log.d(tag, str)
    }
    fun e(str: String) {
        if(BuildConfig.DEBUG)
            android.util.Log.e(tag, str)
    }
}