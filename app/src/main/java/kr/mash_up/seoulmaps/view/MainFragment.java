package kr.mash_up.seoulmaps.view;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kr.mash_up.seoulmaps.R;
import kr.mash_up.seoulmaps.listener.OnItemClickListener;

/**
 * Created by Tak on 2017. 8. 13..
 */

public class MainFragment extends DialogFragment {
    public static final String TAG = MainFragment.class.getSimpleName();
    Unbinder unbinder;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
//        switch ((mNum-1)%6) {
//            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
//            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
//            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
//            case 4: style = DialogFragment.STYLE_NORMAL; break;
//        }
//        switch ((mNum-1)%6) {
//            case 4: theme = android.R.style.Theme_Holo; break;
//            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
//            case 6: theme = android.R.style.Theme_Holo_Light; break;
//            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
//            case 8: theme = android.R.style.Theme_Holo_Light; break;
//        }
        style = DialogFragment.STYLE_NO_TITLE;
        theme = R.style.CustomDialog;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.toilet_layout, R.id.smoke_layout})
    public void onCategoryClicked(View view) {
        String category = null;
        switch (view.getId()) {
            case R.id.toilet_layout:
                category = "toilet";
                break;
            case R.id.smoke_layout:
                category = "smoke";
                break;
        }
        if(onCategoryItemClickListener != null && category != null) {
            this.onCategoryItemClickListener.onItemClick(category);
            this.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private OnItemClickListener onCategoryItemClickListener;
    public void setOnClickListener(OnItemClickListener onCategoryItemClickListener) {
        this.onCategoryItemClickListener = onCategoryItemClickListener;
    }
}
