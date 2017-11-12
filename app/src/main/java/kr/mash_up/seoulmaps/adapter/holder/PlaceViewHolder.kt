package kr.mash_up.seoulmaps.adapter.holder

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.location.places.AutocompletePrediction
import kotlinx.android.synthetic.main.place_autocomplete_item.view.*
import kr.mash_up.seoulmaps.R
import kr.mash_up.seoulmaps.adapter.PlaceAutocompleteAdapter

/**
 * Created by Tak on 2017. 8. 21..
 */

class PlaceViewHolder(context: Context, parent: ViewGroup?, val onPlaceItemClickListener: PlaceAutocompleteAdapter.OnPlaceItemClickListener?) :
        RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.place_autocomplete_item, parent, false)) {

    fun bindView(item: AutocompletePrediction?, position: Int) {
        itemView?.apply {
            place_item_title.text = item?.getPrimaryText(StyleSpan(Typeface.BOLD))
            place_item_delete.setImageResource(R.drawable.item_delete)

            setOnClickListener {
                onPlaceItemClickListener?.onItemClick(item)
            }
        }

//        itemView?.let {
//            with(it) {
//                place_item_title.text = item?.getPrimaryText(StyleSpan(Typeface.BOLD))
//                place_item_delete.setImageResource(R.drawable.item_delete)


//        holder.placeTitle.text = placeItem?.getPrimaryText(STYLE_BOLD)
//        holder.placeDelete.setImageResource(R.drawable.item_delete)
//            }
//        }
    }


//    @BindView(R.id.place_item_title)
//    @BindView(R.id.place_item_delete)
}
