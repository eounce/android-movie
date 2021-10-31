package com.example.cinema

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainInfoData(
    val movieName: String,
    val movieGrade: Int,
    val ratingBar: Float,
    val movieRating: String,
    val mId: Int
):Parcelable