package kr.mash_up.seoulmaps.network

import kr.mash_up.seoulmaps.data.PublicSmokeInfo
import kr.mash_up.seoulmaps.data.PublicToiletInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Tak on 2017. 10. 29..
 */
interface PublicServiceInterface {
    @GET("/location/get/toilet")
    fun getToiletService(
            @Query("lat") userLat: Double?, @Query("lng") userLong: Double?): Call<PublicToiletInfo>

    @GET("/location/get/smoke")
    fun getSmokeService(
            @Query("lat") userLat: Double?, @Query("lng") userLong: Double?): Call<PublicSmokeInfo>
}