package kr.mash_up.seoulmaps.view;

import android.os.Bundle;
import android.support.annotation.UiThread;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import kr.mash_up.seoulmaps.R;
import kr.mash_up.seoulmaps.base.BaseActivity;
import kr.mash_up.seoulmaps.present.MainContract;
import kr.mash_up.seoulmaps.present.MainPresenter;

/**
 * Created by wooyoungki on 2017. 7. 24..
 */

public class MainActivity extends BaseActivity implements MainContract.View, OnMapReadyCallback {

    MainContract.Presenter p;

    private GoogleMap map;

    @Override
    public void onCreate(Bundle savedBundle){
        super.onCreate(savedBundle);

        p = new MainPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        setGoogleMap();
        setUpToolbar();
    }

    private void setGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @UiThread
    private void setUpToolbar() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
    }
}
