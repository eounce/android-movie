package com.example.cinema

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class ReviewAdapter(
    val context: Context,
    val items: ArrayList<ReviewItem>
): BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: ReviewItemView

        if(convertView == null) { view = ReviewItemView(context) }
        else { view = convertView as ReviewItemView }

        val item = items.get(position)
        view.setUserId(item.userId)
        view.setMinute(item.minute)
        view.setReview(item.review)
        view.setRating(item.rating)
        view.setRecommend(item.userCommand)

        return view
    }

    override fun getItem(position: Int): Any {
        return items.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}