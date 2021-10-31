package com.example.cinema

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import com.android.volley.RequestQueue
import com.example.cinema.responseData.MovieInfo
import com.example.cinema.responseData.MovieList
import com.example.cinema.responseData.ReadInfo

class AppHelper {
    companion object {
        var requestQueue: RequestQueue? = null
        var host = "http://boostcourse-appapi.connect.or.kr:10000/movie"

        private var database: SQLiteDatabase? = null
        private val createTableOutlineSql = "create table if not exists outline" +
                "(" +
                "    _id integer PRIMARY KEY autoincrement, " +
                "    id integer, " +
                "    title text, " +
                "    title_eng text, " +
                "    dataValue text, " +
                "    user_rating float, " +
                "    audience_rating float, " +
                "    reviewer_rating float, " +
                "    reservation_rate float, " +
                "    reservation_grade integer, " +
                "    grade integer, " +
                "    thumb text, " +
                "    image text " +
                ")"

        private val insertDataOutlineSql = "insert into outline" +
                "(" +
                "    id, title, title_eng, dataValue, user_rating, audience_rating, reviewer_rating, " +
                "    reservation_rate, reservation_grade, grade, thumb, image" +
                ") " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

        private val selectDataOutlineSql = "select * from outline"

        private val createTableReservationSql = "create table if not exists reservation" +
                "(" +
                "   _id integer PRIMARY KEY autoincrement, " +
                "   title text, " +
                "   reservation_date text, " +
                "   adult_number text, " +
                "   youth_number text" +
                ")"

        private val insertDataReservationSql = "insert into reservation" +
                "(" +
                "   title, reservation_date, adult_number, youth_number" +
                ")" +
                "values(?, ?, ?, ?)"

        private val deleteDataReservationSql = "delete from reservation"

        private val selectDataReservationSql = "select * from reservation order by reservation_date DESC"

        private val createTableMoiveSql = "create table if not exists movie" +
                "(" +
                "   title text, " +
                "   id integer PRIMARY KEY, " +
                "   date text, " +
                "   user_rating float, " +
                "   audience_rating float, " +
                "   reviewer_rating float, " +
                "   reservation_rate float, " +
                "   reservation_grade integer, " +
                "   grade integer, " +
                "   thumb text, " +
                "   image text, " +
                "   photos text, " +
                "   videos text, " +
                "   outlinks text, " +
                "   genre text, " +
                "   duration integer, " +
                "   audience integer, " +
                "   synopsis text, " +
                "   director text, " +
                "   actor text," +
                "   like integer, " +
                "   dislike integer" +
                ")"

        private val insertDataMovieSql = "insert into movie" +
                "(" +
                "   title, id, date, user_rating, audience_rating, reviewer_rating, reservation_rate, reservation_grade, " +
                "   grade, thumb, image, photos, videos, outlinks, genre, duration, audience, synopsis, director, actor, like, dislike" +
                ")" +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "on duplicate key "

        private val selectDataMovieSql = "select * from movie "

        fun openDatabase(context: Context, databaseName: String) {
            database = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
        }

        fun createTable(tableName: String) {
            if(tableName.equals("outline")) {
                if(database != null) {
                    database?.execSQL(createTableOutlineSql)
                }
            } else if(tableName.equals("movie")) {
                if(database != null) {
                    database?.execSQL(createTableMoiveSql)
                }
            } else if(tableName.equals("reservation")) {
                if(database != null) {
                    database?.execSQL(createTableReservationSql)
                }
            }
        }

        fun insertReservationData(title: String, date: String, adult_number: String, youth_number: String) {
            if(database != null) {
                var reservationData = arrayOf(title, date, adult_number, youth_number)
                database?.execSQL(insertDataReservationSql, reservationData)
            }
        }

        fun insertOutlineData(data: ArrayList<MovieList>) {
            if (database != null) {
                database?.execSQL("drop table if exists outline")
                createTable("outline")

                for (i in 0 until data.size) {
                    var id = data.get(i).id
                    var title = data.get(i).title
                    var title_eng = data.get(i).title_eng
                    var dataValue = data.get(i).date
                    var user_rating = data.get(i).user_rating
                    var audience_rating = data.get(i).audience_rating
                    var reviewer_rating = data.get(i).reviewer_rating
                    var reservation_rate = data.get(i).reservation_rate
                    var reservation_grade = data.get(i).reservation_grade
                    var grade = data.get(i).grade
                    var thumb = data.get(i).thumb
                    var image = data.get(i).image
                    var movieData = arrayOf(
                        id, title, title_eng, dataValue, user_rating, audience_rating,
                        reviewer_rating, reservation_rate, reservation_grade, grade, thumb, image
                    )
                    database?.execSQL(insertDataOutlineSql, movieData)
                }
            }
        }

        fun insertMovieData(data: ArrayList<ReadInfo>) {
            if (database != null) {
                for (i in 0 until data.size) {
                    var values = ContentValues()
                    values.put("title", data.get(i).title)
                    values.put("id", data.get(i).id)
                    values.put("date", data.get(i).date)
                    values.put("user_rating", data.get(i).user_rating)
                    values.put("audience_rating", data.get(i).audience_rating)
                    values.put("reviewer_rating", data.get(i).reviewer_rating)
                    values.put("reservation_rate", data.get(i).reservation_rate)
                    values.put("reservation_grade", data.get(i).reservation_grade)
                    values.put("grade", data.get(i).grade)
                    values.put("thumb", data.get(i).thumb)
                    values.put("image", data.get(i).image)
                    values.put("photos", data.get(i).photos)
                    values.put("videos", data.get(i).videos)
                    values.put("outlinks", data.get(i).outlinks)
                    values.put("genre", data.get(i).genre)
                    values.put("duration", data.get(i).duration)
                    values.put("audience", data.get(i).audience)
                    values.put("synopsis", data.get(i).synopsis)
                    values.put("director", data.get(i).director)
                    values.put("actor", data.get(i).actor)
                    values.put("like", data.get(i).like)
                    values.put("dislike", data.get(i).dislike)

                    database?.insertWithOnConflict("movie", null, values, SQLiteDatabase.CONFLICT_REPLACE)
                }
            }
        }

        fun selectData(tableName: String, context: Context, id: Int): Cursor? {
            var cursor: Cursor? = null
            if(tableName.equals("outline")){
                if(database != null) {
                    cursor = database?.rawQuery(selectDataOutlineSql, null)
                    return cursor
                }
            } else if(tableName.equals("movie")) {
                cursor = database?.rawQuery(selectDataMovieSql + "where id=" + id, null)
                return cursor
            } else if(tableName.equals("reservation")) {
                cursor = database?.rawQuery(selectDataReservationSql, null)
                return cursor
            }

            return cursor
        }

        fun deleteDate(tableName: String, id: Int) {
            if(database != null) {
                if(tableName.equals("reservation")){
                    database?.execSQL(deleteDataReservationSql + " where _id = " + id)
                }
            }
        }
    }
}