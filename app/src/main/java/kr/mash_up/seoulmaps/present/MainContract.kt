package kr.mash_up.seoulmaps.present

import com.google.android.gms.location.places.AutocompletePrediction
import kr.mash_up.seoulmaps.adapter.contract.PlaceAdapterContract
import kr.mash_up.seoulmaps.data.model.PublicInfoDataSource

/**
 * Created by wooyoungki on 2017. 7. 24..
 */

interface MainContract {

    interface View {
        fun getPlaceInfo(placeItem: AutocompletePrediction?)
        fun getToiletInfo()
        fun showLoadFail()
    }

    interface Presenter {
        var view: View?

        /**
         * Adapter에 대한 View정의
         */
        var adapterView: PlaceAdapterContract.View?

        /**
         * Model
         */
        var toiletInfo: PublicInfoDataSource?

        /**
         * 공중 화장실 정보를 불러온다.
         */
        fun getPublicToiletInfo(lat: Float?, lng: Float?)
    }
}
