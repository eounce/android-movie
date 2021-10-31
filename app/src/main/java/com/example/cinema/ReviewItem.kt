package com.example.cinema

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class ReviewItem (val userId: String, val minute: Long, val review: String, val rating: Float, val userCommand: Int): Parcelable{
}