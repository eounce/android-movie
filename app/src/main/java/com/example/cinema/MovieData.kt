package com.example.cinema

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieData(
    val movieImage: Int,
    val movieName: String
):Parcelable
