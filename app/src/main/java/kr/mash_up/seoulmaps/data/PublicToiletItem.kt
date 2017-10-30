package kr.mash_up.seoulmaps.data

/**
 * Created by Tak on 2017. 10. 29..
 */
data class PublicToiletItem(val _id: String,
                            val updateDate: String,
                            val insertDate: String,
                            val toiletType: String,
                            val locationName: String,
                            val __v: String,
                            val location: FloatArray)
