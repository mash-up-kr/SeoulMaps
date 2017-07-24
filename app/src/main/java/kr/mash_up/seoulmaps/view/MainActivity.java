package kr.mash_up.seoulmaps.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import kr.mash_up.seoulmaps.present.MainContract;
import kr.mash_up.seoulmaps.present.MainPresenter;

/**
 * Created by wooyoungki on 2017. 7. 24..
 */

public class MainActivity extends AppCompatActivity implements MainContract.View {

    MainContract.Presenter p;

    @Override
    public void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);

        p = new MainPresenter(this);

    }
}
