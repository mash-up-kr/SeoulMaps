package kr.mash_up.seoulmaps.present

import kr.mash_up.seoulmaps.data.model.PublicInfoDataSource

/**
 * Created by wooyoungki on 2017. 7. 24..
 */

interface MainContract {

    interface View {
        fun getToiletInfo()
        fun showLoadFail()
    }

    interface Presenter {
        var view: View?

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
