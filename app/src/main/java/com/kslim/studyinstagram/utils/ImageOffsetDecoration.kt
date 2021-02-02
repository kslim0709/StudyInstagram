package com.kslim.studyinstagram.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ImageOffsetDecoration(private val mImageOffset: Int) : RecyclerView.ItemDecoration() {
    private val mImageHalfOffset: Int = mImageOffset / 2


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)


        val position = parent.getChildAdapterPosition(view)


        if (position < 3) {
            outRect.top = mImageOffset
            outRect.bottom = mImageOffset
        } else {
            outRect.bottom = mImageOffset
        }
        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        when (lp.spanIndex) {
            0 -> {
                outRect.left = mImageOffset
                outRect.right = mImageHalfOffset
            }
            1 -> {
                outRect.left = mImageHalfOffset
                outRect.right = mImageHalfOffset
            }
            2 -> {
                outRect.left = mImageHalfOffset
                outRect.right = mImageOffset
            }
        }

    }

}