package com.example.cinema.responseData

class MovieList(
    val id: Int,
    val title: String,
    val title_eng: String,
    val date: String,
    val user_rating: Float,
    val audience_rating: Float,
    val reviewer_rating: Float,
    val reservation_rate: Float,
    val reservation_grade: Int,
    val grade: Int,
    val thumb: String,
    val image: String
) {}