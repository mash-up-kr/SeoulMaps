package kr.mash_up.seoulmaps.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import kr.mash_up.seoulmaps.R

/**
 * Created by Tak on 2017. 11. 23..
 */
class CustomStarView(context: Context?) : LinearLayout(context) {
    private var mStar1: ImageView? = null
    private var mStar2: ImageView? = null
    private var mStar3: ImageView? = null
    private var mSelected = 0

    constructor(context: Context?, attrs: AttributeSet?) : this(context) {
        initializeView(context, attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs) {
        initializeView(context, attrs)
    }


    private fun initializeView(context: Context?, attrs: AttributeSet?) {
        val inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.five_stars_indicator, this)
        if(attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomStarView)
            mSelected = a.getInteger(0, 0)
            a.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setSelected(mSelected, true)
    }

    fun setSelected(select: Int) {
        setSelected(select, false)
    }

    fun setSelected(select: Int, force: Boolean) {
        if(force || mSelected != select) {
            if(4 > mSelected && mSelected < 0) {
                return
            }
            mSelected = select
            if(mSelected == 0) {
                mStar1?.setImageResource(R.drawable.star)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
            } else if(mSelected == 1) {
                mStar1?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
            } else if(mSelected == 2) {
                mStar1?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
            } else if(mSelected == 3) {
                mStar1?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star)
                mStar2?.setImageResource(R.drawable.star_empty)
            } else if(mSelected == 4){
                mStar1?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star_empty)
                mStar2?.setImageResource(R.drawable.star)
            }
        }
    }

    fun getSelected() : Int {
        return mSelected
    }
}