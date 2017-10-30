package kr.mash_up.seoulmaps.data

/**
 * Created by Tak on 2017. 10. 29..
 */
data class PublicToiletInfo(val message: String,
                            val code: Int,
                            val results: List<PublicToiletItem>)