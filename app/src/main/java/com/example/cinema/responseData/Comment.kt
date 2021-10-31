package com.example.cinema.responseData

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
    val id: Int,
    val writer: String,
    val movieId: Int,
    val writer_image: String?,
    val time: String,
    val timestamp: Long,
    val rating: Float,
    val contents: String,
    val recommend: Int
):Parcelable {}