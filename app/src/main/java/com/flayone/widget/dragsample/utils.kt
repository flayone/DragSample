package com.flayone.widget.dragsample

import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.displayMetrics


val app = BaseApplication.instance

fun dp2px(dp: Float): Int {
    val scale = app.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}


interface BaseClickListener {
    fun onClick()
}

/**
 * 控件拖拽监听器
 */
interface OnDraggableClickListener {

    /**
     * 当控件拖拽完后回调
     *
     * @param v    拖拽控件
     * @param left 控件左边距
     * @param top  控件右边距
     */
    fun onDragged(v: View, left: Int, top: Int)

    /**
     * 当可拖拽控件被点击时回调
     *
     * @param v 拖拽控件
     */
    fun onClick(v: View)
}

//初始化拖拽布局方法，重新layout位置
fun initDragLayout(view: View, heightPercent: Double = (2 / 3).toDouble()) {
    view.run {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusHeight = resources.getDimensionPixelSize(resourceId)
        //需要考虑状态栏的高度
        val h = app.displayMetrics.heightPixels - statusHeight
        val w = resources.displayMetrics.widthPixels

        val mTop = (h * heightPercent).toInt()
        val mRight = w
        val mLeft = w - measuredWidth
        val mBottom = h * 2 / 3 + measuredHeight
        layout(mLeft, mTop, mRight, mBottom)
        val lp = layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(mLeft, mTop, 0, 0)
        layoutParams = lp
    }
}