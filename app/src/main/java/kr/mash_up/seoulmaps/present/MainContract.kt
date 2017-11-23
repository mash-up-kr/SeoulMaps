package kr.mash_up.seoulmaps.present

import com.google.android.gms.location.places.AutocompletePrediction
import kr.mash_up.seoulmaps.adapter.contract.BottomAdapterContract
import kr.mash_up.seoulmaps.adapter.contract.PlaceAdapterContract
import kr.mash_up.seoulmaps.data.BottomSheetItem
import kr.mash_up.seoulmaps.data.PublicToiletItem
import kr.mash_up.seoulmaps.data.model.PublicInfoDataSource

/**
 * Created by wooyoungki on 2017. 7. 24..
 */

interface MainContract {

    interface View {
        fun getPlaceInfo(placeItem: AutocompletePrediction?)
        fun showToiletInfo(publicToiletItem: List<PublicToiletItem>?, lat: Double?, lng: Double?)
        fun showSmokeInfo()
        fun showLoadFail()

        fun showBottomItemInfo(bottomSheetItem: BottomSheetItem?, viewId: Int)
    }

    interface Presenter {
        var view: View?

        /**
         * Adapter에 대한 View정의
         */
        var placeAdapterView: PlaceAdapterContract.View?
        var bottomAdapterView: BottomAdapterContract.View?

        /**
         * Model
         */
        var toiletInfo: PublicInfoDataSource?
        var smokeInfo: PublicInfoDataSource?

        /**
         * 공중 서비스 정보를 불러온다.
         */
        fun getPublicToiletInfo(lat: Double?, lng: Double?)
        fun getPublicSmokeInfo(lat: Double?, lng: Double?)
    }
}
