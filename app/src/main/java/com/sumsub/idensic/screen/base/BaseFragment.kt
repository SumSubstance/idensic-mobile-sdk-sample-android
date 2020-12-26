package com.sumsub.idensic.screen.base

import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.sumsub.idensic.App
import com.sumsub.idensic.common.getSoftInputMode
import com.sumsub.idensic.common.setSoftInputMode
import com.sumsub.idensic.manager.PrefManager

abstract class BaseFragment(@LayoutRes resId: Int): Fragment(resId) {

    private var originalMode: Int? = null

    protected val prefManager: PrefManager get() = (requireActivity().application as App).prefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        originalMode = activity?.window?.getSoftInputMode()
        setSoftInputMode(getSoftInputMode())
    }

    override fun onDestroy() {
        super.onDestroy()
        originalMode?.let { setSoftInputMode(it) }
    }

    protected open fun getSoftInputMode(): Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
}