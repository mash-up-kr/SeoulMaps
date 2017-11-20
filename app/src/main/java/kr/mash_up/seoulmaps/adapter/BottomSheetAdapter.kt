package kr.mash_up.seoulmaps.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import kr.mash_up.seoulmaps.adapter.holder.BottomSheetViewHolder
import kr.mash_up.seoulmaps.data.BottomSheetItem

/**
 * Created by Tak on 2017. 11. 17..
 */

class BottomSheetAdapter(private val mContext: Context) : RecyclerView.Adapter<BottomSheetViewHolder>() {
    private var publicItems: List<BottomSheetItem>? = ArrayList()

    override fun onBindViewHolder(holder: BottomSheetViewHolder?, position: Int) {
        holder?.bindView(getItem(position), position)
    }

    fun getItem(position: Int): BottomSheetItem? = publicItems?.get(position)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BottomSheetViewHolder
                = BottomSheetViewHolder(mContext, parent)

    override fun getItemCount(): Int = publicItems?.size ?: 0

    fun add(publicItem: List<BottomSheetItem>) {
        publicItems = publicItem
        notifyDataSetChanged()
    }

}
