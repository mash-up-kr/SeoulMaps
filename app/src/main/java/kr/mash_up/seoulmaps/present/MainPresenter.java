package kr.mash_up.seoulmaps.present;

/**
 * Created by wooyoungki on 2017. 7. 24..
 */

public class MainPresenter implements MainContract.Presenter {

    MainContract.View view;

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }
}
