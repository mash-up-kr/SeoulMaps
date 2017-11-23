package kr.mash_up.seoulmaps.adapter.contract

import kr.mash_up.seoulmaps.adapter.BottomSheetAdapter
import kr.mash_up.seoulmaps.data.BottomSheetItem

/**
 * Created by Tak on 2017. 11. 17..
 */

interface BottomAdapterContract {
    interface View {
        var onBottomItemClickListener: BottomSheetAdapter.OnBottomPlaceClickListener?
    }

    interface Model {
        fun getItem(position: Int): BottomSheetItem?
    }
}
