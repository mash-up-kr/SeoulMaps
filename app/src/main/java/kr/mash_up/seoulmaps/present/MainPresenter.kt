package kr.mash_up.seoulmaps.present

import com.google.android.gms.location.places.AutocompletePrediction
import kr.mash_up.seoulmaps.adapter.PlaceAutocompleteAdapter
import kr.mash_up.seoulmaps.adapter.contract.PlaceAdapterContract
import kr.mash_up.seoulmaps.data.PublicSmokeInfo
import kr.mash_up.seoulmaps.data.PublicToiletInfo
import kr.mash_up.seoulmaps.data.PublicToiletItem
import kr.mash_up.seoulmaps.data.model.PublicInfoDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by wooyoungki on 2017. 7. 24..
 */

class MainPresenter : MainContract.Presenter {

    override var view: MainContract.View? = null    //MainActivity

    override var toiletInfo: PublicInfoDataSource? = null
    override var smokeInfo: PublicInfoDataSource? = null

    override var adapterView: PlaceAdapterContract.View? = null
    set(value) {
        value?.onPlaceItemClickListener = object : PlaceAutocompleteAdapter.OnPlaceItemClickListener {
            override fun onItemClick(placeItem: AutocompletePrediction?) {
                view?.getPlaceInfo(placeItem)
            }
        }
    }

    override fun getPublicToiletInfo(lat: Float?, lng: Float?) {
        toiletInfo?.getToiletInfoService(lat, lng)?.enqueue(object : Callback<PublicToiletInfo> {
            override fun onResponse(call: Call<PublicToiletInfo>?, response: Response<PublicToiletInfo>?) {
                if(response?.isSuccessful ?: false) {
                    val publicToliletInfo = response?.body()

                    if(publicToliletInfo?.message.equals("Success")) {
                        val publicToiletItem: List<PublicToiletItem>? = publicToliletInfo?.results

                        view?.getToiletInfo(publicToiletItem)
                    } else
                        view?.showLoadFail()
                } else
                    view?.showLoadFail()
            }

            override fun onFailure(call: Call<PublicToiletInfo>?, t: Throwable?) {

            }

        })
    }

    override fun getPublicSmokeInfo(lat: Float?, lng: Float?) {
        smokeInfo?.getSmokeInfoService(lat, lng)?.enqueue(object : Callback<PublicSmokeInfo> {
            override fun onResponse(call: Call<PublicSmokeInfo>?, response: Response<PublicSmokeInfo>?) {
                if(response?.isSuccessful == true) {
                    val publicSmokeInfo = response.body()

                    if(publicSmokeInfo?.message.equals("Success")) {


                    } else
                        view?.showLoadFail()
                } else
                    view?.showLoadFail()
            }

            override fun onFailure(call: Call<PublicSmokeInfo>?, t: Throwable?) {

            }

        })
    }
}
