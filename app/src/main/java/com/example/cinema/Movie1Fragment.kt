package com.example.cinema

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.movie1_fragment.*

class Movie1Fragment: Fragment() {
    lateinit var callback: FragmentCallback
    var mId: Int? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is FragmentCallback) {
            callback = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.movie1_fragment, container, false) as ViewGroup

        val button: Button = rootView.findViewById(R.id.button)
        button.setOnClickListener {
            callback.onCommand("getId", mId ?: 0)
        }

        return rootView
    }

    fun movieInfoFromActivity(
        image: String,
        title: String,
        reservation_rate: Float,
        grade: Int,
        id: Int
    ) {
        movieTitle.text = "1. " + title
        movieRate.text = "예매율 " + reservation_rate.toString() + "%"
        movieGrade.text = grade.toString() + "세 관람가"
        Glide.with(this@Movie1Fragment)
            .load(image)
            .into(movieImage)
        mId = id
    }
}