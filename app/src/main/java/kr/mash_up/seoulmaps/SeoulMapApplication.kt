package kr.mash_up.seoulmaps

import android.app.Application
import android.content.Context

/**
 * Created by Tak on 2017. 8. 13..
 */

internal class SeoulMapApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        lateinit var context: Context
            private set
    }
}


