package com.example.cinema.responseData


//    "message": "movie readCommentList 성공",
//    "code": 200,
//    "resultType": "list",
//    "totalCount": 669,
//    "result": [
//    {
//        "id": 7439,
//        "writer": "bigdragon",
//        "movieId": 4,
//        "writer_image": null,
//        "time": "2020-09-08 16:09:51",
//        "timestamp": 1599548991,
//        "rating": 9.0,
//        "contents": "재미있다",
//        "recommend": 0
//    },

class CommentList(
   val message: String,
   val code: Int,
   val resultType: String,
   val totalCount: Int,
   val result: ArrayList<Comment>
) {}