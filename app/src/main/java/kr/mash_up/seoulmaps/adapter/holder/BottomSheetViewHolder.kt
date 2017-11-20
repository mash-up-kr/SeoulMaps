package kr.mash_up.seoulmaps.adapter.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bottom_sheet_item.view.*
import kr.mash_up.seoulmaps.R
import kr.mash_up.seoulmaps.data.BottomSheetItem

/**
 * Created by Tak on 2017. 11. 17..
 */

class BottomSheetViewHolder(context: Context, parent: ViewGroup?) :
        RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.bottom_sheet_item, parent, false)) {

    fun bindView(item: BottomSheetItem?, position: Int) {
        itemView?.apply {
            img.setImageResource(R.drawable.toilet)
            title.setText(item?.title)
            type.setText(item?.type)
            distance.setText(item?.distance)
        }
    }
}
