package com.aidevu.signage.service

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPreference(c: Context) {

    private var mContext: Context? = null
    private var pref: SharedPreferences? = null

    init {
        mContext = c
        val prefName = c.packageName
        pref = mContext?.getSharedPreferences(prefName, Activity.MODE_PRIVATE)
    }

    companion object {

        const val KEY_COOKIE = "com.aidevu.signage.key.cookie"
        private var dsp: SharedPreference? = null

        fun getInstanceOf(c: Context): SharedPreference? {
            if (dsp == null) {
                dsp = SharedPreference(c)
            }
            return dsp
        }
    }

    fun putHashSet(key: String?, set: HashSet<String?>?) {
        val editor = pref?.edit()
        editor?.let {
            editor.putStringSet(key, set)
            editor.commit()
        }

    }

    fun removeHashSet() {
        val editor = pref?.edit()
        editor?.let {
            editor.remove(KEY_COOKIE)
            editor.commit()
        }
    }

    fun getHashSet(key: String?, dftValue: HashSet<String?>?): HashSet<String?>? {
        return try {
            pref?.getStringSet(key, dftValue) as HashSet<String?>?
        } catch (e: Exception) {
            e.printStackTrace()
            dftValue
        }
    }
}