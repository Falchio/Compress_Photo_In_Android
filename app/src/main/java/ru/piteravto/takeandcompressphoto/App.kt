package ru.piteravto.takeandcompressphoto

import android.app.Application
import android.content.Context
import android.content.res.Resources

class App: Application() {

    companion object{
        private lateinit var INSTANCE: App
        val context: Context get() =  INSTANCE.applicationContext
        val resources: Resources get() = INSTANCE.resources
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}