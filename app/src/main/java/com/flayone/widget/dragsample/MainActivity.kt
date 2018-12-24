package com.flayone.widget.dragsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drag.setOnClickListener(object : BaseClickListener {
            override fun onClick() {
                toast("点击事件：显示粉色Android")
                cl_drag.visibility = View.VISIBLE
            }
        })
        cl_drag.setOnTouchListener(object : OnDragListener(true, object : OnDraggableClickListener {
            override fun onDragged(v: View, left: Int, top: Int) {
                toast("拖拽了粉色Android")
            }

            override fun onClick(v: View) {
                toast("点击了粉色Android")
            }

        }) {})
        img_close.onClick {
            cl_drag.visibility = View.GONE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            initDragLayout(cl_drag, 0.3)
        }
    }
}
