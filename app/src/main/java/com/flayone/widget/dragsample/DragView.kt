package com.flayone.widget.dragsample

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton

class DragView : ImageButton {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0//屏幕宽高
    private var mOriginalX: Float = 0.toFloat()
    private var mOriginalY: Float = 0.toFloat()//手指按下时的初始位置
    private var mOriginalRowX: Float = 0.toFloat()
    private var mOriginalRowY: Float = 0.toFloat()//手指滑动距离
    private var hasAutoPullToBorder: Boolean = true//标记是否开启自动拉到边缘功能
    private var mListener: BaseClickListener? = null

    //记录控件的四边距离对应边框数值
    private var mLeft = 0
    private var mRight = 0
    private var mTop = 0
    private var mBottom = 0


    private fun init() {
    }

    //布局绘制流程走完后，初始化位置
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            initDragLayout(this, 0.6)
        }
    }

    //是否自动贴边
    fun isAuto(b: Boolean) {
        hasAutoPullToBorder = b
    }

    //控件被点击事件
    fun setOnClickListener(listener: BaseClickListener) {
        mListener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusHeight = resources.getDimensionPixelSize(resourceId)
        mScreenWidth = resources.displayMetrics.widthPixels
        //需要考虑状态栏的高度
        mScreenHeight = resources.displayMetrics.heightPixels - statusHeight
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mOriginalRowX = event.rawX
                mOriginalRowY = event.rawY
                mOriginalX = event.x
                mOriginalY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val xx = (event.x - mOriginalX).toInt()//x坐标移动的距离
                val yy = (event.y - mOriginalY).toInt()//y坐标移动的距离
                mLeft = left + xx
                mRight = right + xx
                mTop = top + yy
                mBottom = bottom + yy
                //当水平或者垂直滑动距离大于10,才算拖动事件,过滤滑动超出手机边界的移动
                if (Math.abs(xx) > 20 || Math.abs(yy) > 20) {
                    if (mLeft < 0) {
                        mLeft = 0
                        mRight = measuredWidth
                    }
                    if (mRight > mScreenWidth) {
                        mRight = mScreenWidth
                        mLeft = mScreenWidth - measuredWidth
                    }
                    if (mTop < 0) {
                        mTop = 0
                        mBottom = measuredHeight
                    }
                    if (mBottom > mScreenHeight - measuredHeight / 2) {
                        mBottom = mScreenHeight
                        mTop = mScreenHeight - measuredHeight
                    }
                    layout(mLeft, mTop, mRight, mBottom)
                }
            }
            MotionEvent.ACTION_UP -> {
                //如果移动距离过小，则判定为点击
                if (Math.abs(event.rawX - mOriginalRowX) < dp2px(5f) && Math.abs(event.rawY - mOriginalRowY) < dp2px(5f)) {
                    mListener?.onClick()
                    return true
                }
                //在拖动过按钮后，如果其他view刷新导致重绘，会让按钮重回原点，所以需要更改布局参数
                startAutoPull(layoutParams as ViewGroup.MarginLayoutParams)
                //消除IDE警告
                performClick()
            }
        }
        return true
    }

    /**
     * 开启自动拖拽
     * @param lp 控件布局参数
     */
    private fun startAutoPull(lp: ViewGroup.MarginLayoutParams) {
        if (!hasAutoPullToBorder) {
            //定格在拖拽完抬手时的位置
            layout(mLeft, mTop, mRight, mBottom)
            lp.setMargins(mLeft, mTop, 0, 0)
            layoutParams = lp
            //TODO can add drag finishListener here
            return
        }
        //当用户拖拽完后，让控件根据远近距离回到最近的边缘
        if (left + measuredWidth / 2 <= mScreenWidth / 2) {
            mLeft = 0
            mRight = measuredWidth
        } else {
            mLeft = mScreenWidth - measuredWidth
            mRight = mScreenWidth
        }
        val animator = ValueAnimator.ofFloat(left.toFloat(), mLeft.toFloat())
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val leftMargin = (animation.animatedValue as Float).toInt()
            layout(mLeft, mTop, mRight, mBottom)
            lp.setMargins(leftMargin, mTop, 0, 0)
            layoutParams = lp
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                //TODO can add Auto drag finishListener here
            }
        })
        animator.duration = 400
        animator.start()
    }
}