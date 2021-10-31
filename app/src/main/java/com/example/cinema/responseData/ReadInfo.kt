package com.example.cinema.responseData

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReadInfo(
    val title: String,
    val id: Int,
    val date: String,
    val user_rating: Float,
    val audience_rating: Float,
    val reviewer_rating: Float,
    val reservation_rate: Float,
    val reservation_grade: Int,
    val grade: Int,
    val thumb: String,
    val image: String,
    val photos: String?,
    val videos: String?,
    val outlinks: String?,
    val genre: String,
    val duration: Int,
    val audience: Int,
    val synopsis: String,
    val director: String,
    val actor: String,
    val like: Int,
    val dislike: Int
):Parcelable {}