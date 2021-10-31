package com.example.cinema

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReviewData(
    val contents: String,
    val rating: Float
): Parcelable