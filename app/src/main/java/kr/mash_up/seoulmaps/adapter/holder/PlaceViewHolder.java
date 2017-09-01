package kr.mash_up.seoulmaps.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.mash_up.seoulmaps.R;

/**
 * Created by Tak on 2017. 8. 21..
 */

public class PlaceViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.place_item_title) public TextView placeTitle;
    @BindView(R.id.place_item_delete) public ImageView placeDelete;

    public PlaceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
