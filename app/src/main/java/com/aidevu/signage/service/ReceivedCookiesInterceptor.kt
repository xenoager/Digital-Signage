package com.aidevu.signage.service

import android.content.Context
import com.aidevu.signage.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ReceivedCookiesInterceptor(context: Context): Interceptor {

    private var mDsp: SharedPreference? = null

    init {
        mDsp = SharedPreference.getInstanceOf(context)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        var sessionHeader = "_X_"
        var sessionSP: String? = "_X_"
        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            val cookies = HashSet<String?>()
            for (header in originalResponse.headers("Set-Cookie")) {
                sessionHeader = "_X_"
                if (header.contains("JSESSIONID")) {
                    sessionHeader = header
                    break
                }
            }
            val preferences = mDsp?.getHashSet(SharedPreference.KEY_COOKIE, HashSet())
            if (preferences != null) {
                for (cookie in preferences) {
                    if (!Utils.isEmptyTrimmed(cookie)) {
                        if (cookie?.contains("JSESSIONID") == true) {
                            sessionSP = cookie
                            break
                        }
                    }
                }
            }
            if ("_X_" != sessionHeader && sessionHeader != sessionSP) {
                cookies.add(sessionHeader)
                mDsp?.putHashSet(SharedPreference.KEY_COOKIE, cookies)
            } else if ("_X_" == sessionHeader && "_X_" != sessionSP) {
                cookies.add(sessionSP)
                mDsp?.putHashSet(SharedPreference.KEY_COOKIE, cookies)
            }
        }
        return originalResponse
    }
}