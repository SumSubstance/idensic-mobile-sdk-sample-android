package com.sumsub.idensic.common

import android.view.Window
import androidx.fragment.app.Fragment

fun Fragment.getWindow(): Window? = activity?.window

fun Fragment.setSoftInputMode(mode: Int) = getWindow()?.setSoftInputMode(mode)

fun Window.getSoftInputMode(): Int = attributes.softInputMode