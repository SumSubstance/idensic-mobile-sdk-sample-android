package com.sumsub.idensic.manager

import com.sumsub.idensic.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URLEncoder

class DemoHeadersInterceptor(val isSandBox: () -> Boolean, val clientId: () -> String?) : Interceptor {

    @Synchronized
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
            .addHeader(HEADER_CLIENT_ID, "msdkDemo")
            .addHeader(HEADER_APPLICATION, "com.sumsub.IdensicMobileSDK-Android-Public_Demo_Kotlin")
            .addHeader(HEADER_APPLICATION_VERSION, BuildConfig.DEMO_VERSION)
            .addHeader(HEADER_OS, "Android")

        if (isSandBox()) {
            requestBuilder.addHeader(HEADER_COOKIE, "_ss_route=sbx")
        }

        clientId()?.takeIf { it.isNotBlank() }?.let {
            requestBuilder.addHeader(HEADER_X_IMPERSONATE, URLEncoder.encode(it, "utf-8"))
        }

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        private const val HEADER_COOKIE = "Cookie"
        private const val HEADER_CLIENT_ID = "X-Client-Id"
        private const val HEADER_APPLICATION = "X-Mob-App"
        private const val HEADER_APPLICATION_VERSION = "X-Mob-App-Ver"
        private const val HEADER_OS = "X-Mob-OS"
        private const val HEADER_X_IMPERSONATE = "X-Impersonate"
    }
}