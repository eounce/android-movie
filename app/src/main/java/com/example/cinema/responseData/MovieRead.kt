package com.example.cinema.responseData

class MovieRead(
    val message: String,
    val code: Int,
    val resultType: String,
    val result: ArrayList<ReadInfo>
) {}