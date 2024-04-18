package com.aidevu.signage.service

import android.content.Context
import com.aidevu.signage.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import okhttp3.Request

class AddCookiesInterceptor(context: Context): Interceptor {

    private var mDsp: SharedPreference? = null

    init {
        mDsp = context?.let {
            SharedPreference.getInstanceOf(it)
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        val preferences = mDsp?.getHashSet(SharedPreference.KEY_COOKIE, HashSet())
        if (preferences != null) {
            for (cookie in preferences) {
                if (!Utils.isEmptyTrimmed(cookie)) {
                    if(cookie != null) {
                        builder.addHeader("Cookie", cookie)
                    }
                }
            }
        }

        return chain.proceed(builder.build())
    }
}