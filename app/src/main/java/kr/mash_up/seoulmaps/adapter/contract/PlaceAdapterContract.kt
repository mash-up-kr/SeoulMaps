package kr.mash_up.seoulmaps.adapter.contract

import kr.mash_up.seoulmaps.adapter.PlaceAutocompleteAdapter

/**
 * Created by Tak on 2017. 8. 20..
 */

interface PlaceAdapterContract {
    interface View {
        var onPlaceItemClickListener: PlaceAutocompleteAdapter.OnPlaceItemClickListener?
    }

    interface Model {

    }
}
