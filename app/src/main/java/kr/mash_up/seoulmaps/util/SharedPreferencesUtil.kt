package kr.mash_up.seoulmaps.util

import android.content.SharedPreferences
import android.preference.PreferenceManager
import kr.mash_up.seoulmaps.SeoulMapApplication

/**
 * Created by Tak on 2017. 9. 9..
 */
class SharedPreferencesUtil {

    val prefs: SharedPreferences? by lazy { PreferenceManager.getDefaultSharedPreferences(SeoulMapApplication.context) }

    companion object {
        private var instance: SharedPreferencesUtil? = null

        fun getInstances(): SharedPreferencesUtil? {
            if(instance == null)
                SharedPreferencesUtil

            return instance
        }
    }
    //위도, 경도
    var userLat: Float?
        get() = prefs?.getFloat("userLat", -1.toFloat())
        set(value) {
            prefs?.edit()?.let {
                if(value == null)
                    it.remove("userLat")
                else
                    it.putFloat("userLat", value)
                it.commit()
            }
        }

    var userLong: Float?
    get() = prefs?.getFloat("userLong", -1.toFloat())
    set(value) {
        prefs?.edit()?.let {
            if(value == null)
                it.remove("userLong")
            else
                it.putFloat("userLong", value)
            it.commit()
        }
    }

}
//var userId: String?
//    get() = prefs?.getString("userId", null)
//    set(value) {
//        prefs?.edit()?.let {
//            if (value == null)
//                it.remove("userId")
//            else
//                it.putString("userId", value)
//            it.commit()
//        }
//    }