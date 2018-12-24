package com.flayone.widget.dragsample

import android.app.Application

class BaseApplication : Application() {
    //单例模式
    companion object {
        lateinit var instance: BaseApplication //延迟加载，不需要初始化，否则需要在构造函数初始化
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}