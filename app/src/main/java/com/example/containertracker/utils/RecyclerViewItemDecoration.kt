package com.example.containertracker.utils

import android.graphics.Canvas
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.R

/**
 * Created by yovi.putra on 20/07/22"
 * Project name: Container Tracker
 **/

class RecyclerViewItemDecoration(
    private val margin: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        drawVerticalDividers(c, parent)
    }

    private fun drawVerticalDividers(
        c: Canvas,
        parent: RecyclerView
    ) {
        // divider to be drawn below of parent's item
        val drawable = ContextCompat.getDrawable(parent.context, R.drawable.divider) ?: return
        // divider bound
        val leftBound = parent.left + margin
        val rightBound = parent.right - margin
        // draw divider
        for (i in 0 until parent.childCount) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val topBound: Int = child.bottom + params.bottomMargin
            val bottomBound: Int = topBound + drawable.intrinsicHeight
            drawable.setBounds(leftBound, topBound, rightBound, bottomBound)
            drawable.draw(c)
        }
    }
}