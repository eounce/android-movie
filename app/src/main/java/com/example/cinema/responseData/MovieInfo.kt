package com.example.cinema.responseData

class MovieInfo(
    val message: String,
    val code: Int,
    val resultType: String,
    val result: ArrayList<MovieList>
) {}