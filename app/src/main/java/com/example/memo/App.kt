package com.example.memo

import android.app.Application
import android.content.Context
import com.example.memo.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
Created by chene on @date 20-11-20 上午8:31
 **/
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(mainActivityModule, noteEditActivityModule, appDataBaseModule, classModule)
        }
    }

    companion object {
        var context: Context? = null
            private set
    }
}