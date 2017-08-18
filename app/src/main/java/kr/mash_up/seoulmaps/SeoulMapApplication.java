package kr.mash_up.seoulmaps;

import android.app.Application;
import android.content.Context;

/**
 * Created by Tak on 2017. 8. 13..
 */

public class SeoulMapApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
