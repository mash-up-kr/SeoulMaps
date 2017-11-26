package kr.mash_up.seoulmaps.adapter.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bottom_sheet_item.view.*
import kr.mash_up.seoulmaps.R
import kr.mash_up.seoulmaps.adapter.BottomSheetAdapter
import kr.mash_up.seoulmaps.data.BottomSheetItem

/**
 * Created by Tak on 2017. 11. 17..
 */

class BottomSheetViewHolder(mContext: Context, parent: ViewGroup?, private val onBottomItemClickListener: BottomSheetAdapter.OnBottomPlaceClickListener?) :
        RecyclerView.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_item, parent, false)) {

    fun bindView(item: BottomSheetItem?, position: Int) {
        itemView?.apply {
            img.setImageResource(R.drawable.ic_toilet)
            title.setText(item?.title)
            type.setText(item?.type)
            distance.setText(item?.distance)

            setOnClickListener {
                if(review_container.visibility == View.VISIBLE)
                    review_container.visibility = View.GONE
                else
                    review_container.visibility = View.VISIBLE

                val viewId = R.id.review_container
                onBottomItemClickListener?.onItemClick(item, viewId)
            }
        }
    }
}
