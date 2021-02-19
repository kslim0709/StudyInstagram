package com.kslim.studyinstagram.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class PhotoViewPager(context: Context, attributeSet: AttributeSet? = null) :
    ViewPager(context, attributeSet) {

    private var isLocked: Boolean = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!isLocked) {
            return try {
                super.onInterceptTouchEvent(ev)
            } catch (e: IllegalArgumentException) {
                false
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return !isLocked && super.onTouchEvent(event)
    }
}