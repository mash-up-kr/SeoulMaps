package kr.mash_up.seoulmaps.view

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.five_stars_indicator.view.*
import kr.mash_up.seoulmaps.R

/**
 * Created by Tak on 2017. 11. 23..
 */

class CustomStarView : LinearLayout {
    private var mSelected = 0

    private lateinit var mStar1 : ImageView
    private lateinit var mStar2 : ImageView
    private lateinit var mStar3 : ImageView
    private lateinit var mStar4 : ImageView
    private lateinit var mStar5 : ImageView

    constructor(context: Context) : super(context) {
        initializeView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
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

        mStar1 = findViewById(R.id.star1) as ImageView
        mStar2 = findViewById(R.id.star2) as ImageView
        mStar3 = findViewById(R.id.star3) as ImageView
        mStar4 = findViewById(R.id.star4) as ImageView
        mStar5 = findViewById(R.id.star5) as ImageView

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
                star1?.setImageResource(R.drawable.star)
                star2?.setImageResource(R.drawable.star_empty)
                star3?.setImageResource(R.drawable.star_empty)
                star4?.setImageResource(R.drawable.star_empty)
                star5?.setImageResource(R.drawable.star_empty)
            } else if(mSelected == 1) {
                star1?.setImageResource(R.drawable.star_empty)
                star2?.setImageResource(R.drawable.star)
                star3?.setImageResource(R.drawable.star_empty)
                star4?.setImageResource(R.drawable.star_empty)
                star5?.setImageResource(R.drawable.star_empty)
            } else if(mSelected == 2) {
                star1?.setImageResource(R.drawable.star_empty)
                star2?.setImageResource(R.drawable.star_empty)
                star3?.setImageResource(R.drawable.star)
                star4?.setImageResource(R.drawable.star_empty)
                star5?.setImageResource(R.drawable.star_empty)
            } else if(mSelected == 3) {
                star1?.setImageResource(R.drawable.star_empty)
                star2?.setImageResource(R.drawable.star_empty)
                star3?.setImageResource(R.drawable.star_empty)
                star4?.setImageResource(R.drawable.star)
                star5?.setImageResource(R.drawable.star_empty)
            } else if(mSelected == 4){
                star1?.setImageResource(R.drawable.star_empty)
                star2?.setImageResource(R.drawable.star_empty)
                star3?.setImageResource(R.drawable.star_empty)
                star4?.setImageResource(R.drawable.star_empty)
                star5?.setImageResource(R.drawable.star)
            }
        }
    }

    fun getSelected() : Int {
        return mSelected
    }
}