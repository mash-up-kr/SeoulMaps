package kr.mash_up.seoulmaps

import android.app.Application
import android.content.Context
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

/**
 * Created by Tak on 2017. 8. 13..
 */

internal class SeoulMapApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        setupLeakCanary();
    }

    protected fun setupLeakCanary(): RefWatcher? {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    companion object {
        lateinit var context: Context
            private set
    }
}


