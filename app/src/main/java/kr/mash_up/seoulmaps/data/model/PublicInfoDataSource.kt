package kr.mash_up.seoulmaps.data.model

import kr.mash_up.seoulmaps.network.PublicServiceInterface
import kr.mash_up.seoulmaps.network.createRetrofit

/**
 * Created by Tak on 2017. 10. 29..
 */
object PublicInfoDataSource {
    val TOILET_URL = "http://52.78.80.125:3000"

    private val publicServiceInterface: PublicServiceInterface

    init {
        publicServiceInterface = createRetrofit(PublicServiceInterface::class.java, TOILET_URL)
    }

    fun getToiletInfoService(lat: Float?, lng: Float?) = publicServiceInterface.getToiletService(lat, lng)
}