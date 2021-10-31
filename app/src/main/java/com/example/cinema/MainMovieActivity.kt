package com.example.cinema

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cinema.responseData.MovieInfo
import com.example.cinema.responseData.MovieRead
import com.example.cinema.responseData.ReadInfo
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main_movie.*

class MainMovieActivity : AppCompatActivity(), FragmentCallback {
    lateinit var mDrawerLayout: DrawerLayout
    lateinit var fragment : ArrayList<Fragment>
    private val databaseName = "movie"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_movie)

        AppHelper.openDatabase(applicationContext, databaseName)
        AppHelper.createTable("outline")
        AppHelper.createTable("movie")

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView:NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            it.setChecked(true)
            mDrawerLayout.closeDrawers()

            val id = it.itemId
            when(id) {
                R.id.movieList -> {
                }
                R.id.movieReview -> {
                    val intent = Intent(applicationContext, SearchMovieActivity::class.java)
                    startActivity(intent)
                }
                R.id.movieBook -> {
                    AppHelper.openDatabase(applicationContext, "movie")
                    AppHelper.createTable("reservation")
                    val intent = Intent(applicationContext, ReservationChcekActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        fragment = ArrayList<Fragment>()
        fragment.add(Movie1Fragment())
        fragment.add(Movie2Fragment())
        fragment.add(Movie3Fragment())
        fragment.add(Movie4Fragment())
        fragment.add(Movie5Fragment())

        val adapter = MoviePagerAdapter(supportFragmentManager)
        adapter.addItem(fragment.get(0))
        adapter.addItem(fragment.get(1))
        adapter.addItem(fragment.get(2))
        adapter.addItem(fragment.get(3))
        adapter.addItem(fragment.get(4))
        pager.offscreenPageLimit = 5
        pager.adapter = adapter

        // 네트워킹
        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(applicationContext)
        }
        requestMovieList()
    }

    override fun onCommand(command: String, data: Int) {
        when(command) {
            "getId" -> {
                requestLeadMovie(data)
            }
        }
    }

    fun requestLeadMovie(id: Int) {
        var url = AppHelper.host + "/readMovie"
        url += "?id=" + id

        val request = object : StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                readMovieResponse(it)
            },
            Response.ErrorListener {
                Toast.makeText(applicationContext, "인터넷이 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
                disreadMovieResponse(id)
            }
        ){}

        request.setShouldCache(false)
        AppHelper.requestQueue?.add(request)
    }

    fun readMovieResponse(response: String) {
        var gson = Gson()
        val movieRead = gson.fromJson(response, MovieRead::class.java)
        AppHelper.insertMovieData(movieRead.result)

        if(movieRead != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("movieRead", movieRead.result.get(0))
            startActivity(intent)
        }
    }

    fun disreadMovieResponse(id: Int) {
        val cursor = AppHelper.selectData("movie", applicationContext, id) ?: return
        cursor.moveToNext()
        val readInfo = ReadInfo(
            cursor.getString(cursor.getColumnIndex("title")),
            cursor.getInt(cursor.getColumnIndex("id")),
            cursor.getString(cursor.getColumnIndex("date")),
            cursor.getFloat(cursor.getColumnIndex("user_rating")),
            cursor.getFloat(cursor.getColumnIndex("audience_rating")),
            cursor.getFloat(cursor.getColumnIndex("reviewer_rating")),
            cursor.getFloat(cursor.getColumnIndex("reservation_rate")),
            cursor.getInt(cursor.getColumnIndex("reservation_grade")),
            cursor.getInt(cursor.getColumnIndex("grade")),
            cursor.getString(cursor.getColumnIndex("thumb")),
            cursor.getString(cursor.getColumnIndex("image")),
            cursor.getString(cursor.getColumnIndex("photos")),
            cursor.getString(cursor.getColumnIndex("videos")),
            cursor.getString(cursor.getColumnIndex("outlinks")),
            cursor.getString(cursor.getColumnIndex("genre")),
            cursor.getInt(cursor.getColumnIndex("duration")),
            cursor.getInt(cursor.getColumnIndex("audience")),
            cursor.getString(cursor.getColumnIndex("synopsis")),
            cursor.getString(cursor.getColumnIndex("director")),
            cursor.getString(cursor.getColumnIndex("actor")),
            cursor.getInt(cursor.getColumnIndex("like")),
            cursor.getInt(cursor.getColumnIndex("dislike"))
        )

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("movieRead", readInfo)
        startActivity(intent)
        cursor.close()
    }

    fun requestMovieList() {
        var url = AppHelper.host + "/readMovieList"
        url += "?type=1"

        val request = object : StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                processResponse(it)
            },
            Response.ErrorListener {
                disProcessResponse()
                Toast.makeText(applicationContext, "인터넷이 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        ){}

        request.setShouldCache(false)
        AppHelper.requestQueue?.add(request)
    }

    fun processResponse(response: String) {
        var num = 0
        val gson = Gson()
        val movieInfo = gson.fromJson(response, MovieInfo::class.java)
        AppHelper.insertOutlineData(movieInfo.result)

        if(fragment.size != 0) {
            for (i in 0 until movieInfo.result.size) {
                var title = movieInfo.result.get(i).title
                var reservation_rate = movieInfo.result.get(i).reservation_rate
                var image = movieInfo.result.get(i).image
                var grade = movieInfo.result.get(i).grade
                var id = movieInfo.result.get(i).id

                when (num) {
                    0 -> {
                        val movieFragment = fragment.get(i) as Movie1Fragment
                        movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                    }
                    1 -> {
                        val movieFragment = fragment.get(i) as Movie2Fragment
                        movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                    }
                    2 -> {
                        val movieFragment = fragment.get(i) as Movie3Fragment
                        movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                    }
                    3 -> {
                        val movieFragment = fragment.get(i) as Movie4Fragment
                        movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                    }
                    4 -> {
                        val movieFragment = fragment.get(i) as Movie5Fragment
                        movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                    }
                }
                num += 1
            }
        }
    }

    fun disProcessResponse() {
        val cursor = AppHelper.selectData("outline", applicationContext, 0) ?: return
        var num = 0
        for (i in 0 until cursor?.count) {
            cursor.moveToNext()

            var title = cursor.getString(cursor.getColumnIndex("title"))
            var reservation_rate = cursor.getFloat(cursor.getColumnIndex("reservation_rate"))
            var image = cursor.getString(cursor.getColumnIndex("image"))
            var grade = cursor.getInt(cursor.getColumnIndex("grade"))
            var id = cursor.getInt(cursor.getColumnIndex("id"))

            when (num) {
                0 -> {
                    val movieFragment = fragment.get(i) as Movie1Fragment
                    movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                }
                1 -> {
                    val movieFragment = fragment.get(i) as Movie2Fragment
                    movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                }
                2 -> {
                    val movieFragment = fragment.get(i) as Movie3Fragment
                    movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                }
                3 -> {
                    val movieFragment = fragment.get(i) as Movie4Fragment
                    movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                }
                4 -> {
                    val movieFragment = fragment.get(i) as Movie5Fragment
                    movieFragment.movieInfoFromActivity(image, title, reservation_rate, grade, id)
                }
            }
            num += 1
        }
        cursor.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class MoviePagerAdapter: FragmentStatePagerAdapter {
        constructor(fm: FragmentManager, behavior: Int = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) : super(fm, behavior)
        val items = ArrayList<Fragment>()

        override fun getItem(position: Int): Fragment {
            return items.get(position)
        }

        override fun getCount(): Int {
            return items.size
        }

        fun addItem(item: Fragment) {
            items.add(item)
        }
    }
}