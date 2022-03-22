package ru.piteravto.takeandcompressphoto

import android.app.Application
import android.content.Context

class App: Application() {

    companion object{
        private lateinit var INSTANCE: App
        val context: Context get() =  INSTANCE.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}