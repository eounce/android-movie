package com.example.cinema.responseData

//{
//    "lastBuildDate": "Mon, 12 Oct 2020 22:33:14 +0900",
//    "total": 4,
//    "start": 1,
//    "display": 4,
//    "items": [
//    {
//        "title": "<b>기생충</b>",
//        "link": "https://movie.naver.com/movie/bi/mi/basic.nhn?code=161967",
//        "image": "https://ssl.pstatic.net/imgmovie/mdi/mit110/1619/161967_P80_151640.jpg",
//        "subtitle": "PARASITE",
//        "pubDate": "2019",
//        "director": "봉준호|",
//        "actor": "송강호|이선균|조여정|최우식|박소담|이정은|장혜진|",
//        "userRating": "8.48"
//    },
class SearchMovieList(
   val items : ArrayList<SearchMovie>
) {}