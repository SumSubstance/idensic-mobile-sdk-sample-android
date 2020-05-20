package com.sumsub.idensic.common

import android.app.Activity
import android.content.Context
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.getWindow(): Window? = activity?.window

fun Fragment.getSoftInputMode(): Int? = getWindow()?.getSoftInputMode()

fun Fragment.setSoftInputMode(mode: Int) = getWindow()?.setSoftInputMode(mode)

fun Fragment.hideKeyboard() = activity?.hideKeyboard()

fun Fragment.showKeyboard() {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED,  InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.hideKeyboard() {
    val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Window.getSoftInputMode(): Int = attributes.softInputMode