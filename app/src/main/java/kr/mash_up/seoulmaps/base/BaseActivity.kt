package kr.mash_up.seoulmaps.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        initView()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    @UiThread
    abstract fun initView()
}
