package com.example.cinema

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.review_item_view.view.*

class ReviewItemView:LinearLayout {
    constructor(context: Context?) : super(context) { init(context) }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init(context) }

    fun init(context: Context?){
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.review_item_view, this@ReviewItemView, true)
    }

    fun setUserId(userid: String) {
        userId_view.setText(userid)
    }

    fun setMinute(minute: Long) {
        if(minute == 0L) minute_view.text = "오늘"
        else minute_view.text = minute.toString() + "일전"
    }

    fun setReview(review: String) {
        review_view.setText(review)
    }

    fun setRating(rating: Float) {
        ratingBar.rating = rating
    }

    fun setRecommend(userRecommend: Int) {
        recommend.text = "추천 " + userRecommend.toString()
    }
}