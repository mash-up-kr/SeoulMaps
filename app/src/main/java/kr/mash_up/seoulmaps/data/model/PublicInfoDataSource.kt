package kr.mash_up.seoulmaps.data.model

import kr.mash_up.seoulmaps.network.PublicServiceInterface
import kr.mash_up.seoulmaps.network.createRetrofit

/**
 * Created by Tak on 2017. 10. 29..
 */
object PublicInfoDataSource {
    val BASE_URL = "http://52.78.80.125:3000"

    private val publicServiceInterface: PublicServiceInterface

    init {
        publicServiceInterface = createRetrofit(PublicServiceInterface::class.java, BASE_URL)
    }

    fun getToiletInfoService(lat: Double?, lng: Double?) = publicServiceInterface.getToiletService(lat, lng)
    fun getSmokeInfoService(lat: Double?, lng: Double?) = publicServiceInterface.getSmokeService(lat, lng)
}