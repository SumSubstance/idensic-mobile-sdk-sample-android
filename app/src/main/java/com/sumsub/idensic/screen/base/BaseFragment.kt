package com.sumsub.idensic.screen.base

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.sumsub.idensic.common.getSoftInputMode
import com.sumsub.idensic.common.setSoftInputMode
import kotlinx.coroutines.*

abstract class BaseFragment(@LayoutRes resId: Int): Fragment(resId) {

    private lateinit var job: Job
    protected lateinit var uiScope: CoroutineScope
    protected lateinit var handler: Handler
    private var originalMode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        originalMode = activity?.window?.getSoftInputMode()
        setSoftInputMode(getSoftInputMode())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = SupervisorJob()
        uiScope = CoroutineScope(Dispatchers.Main.immediate + job)
        handler = Handler()
    }

    override fun onDestroyView() {
        uiScope.cancel()
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        originalMode?.let { setSoftInputMode(it) }
    }

    protected open fun getSoftInputMode(): Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
}