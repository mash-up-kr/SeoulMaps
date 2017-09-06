package kr.mash_up.seoulmaps

import android.app.Application
import android.content.Context

/**
 * Created by Tak on 2017. 8. 13..
 */

class SeoulMapApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        var context: Context? = null
            private set
    }
}


