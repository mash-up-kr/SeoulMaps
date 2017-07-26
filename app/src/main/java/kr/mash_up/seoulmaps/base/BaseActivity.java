package kr.mash_up.seoulmaps.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        unbinder = ButterKnife.bind(this);

        initView();
    }


    @LayoutRes
    public abstract int getLayoutId();

    @UiThread
    public abstract void initView();
    

}
