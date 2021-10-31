package com.example.cinema

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.cinema.responseData.CommentList
import com.example.cinema.responseData.MovieInfo
import com.example.cinema.responseData.MovieList
import com.example.cinema.responseData.ReadInfo
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_all_review.*
import kotlinx.android.synthetic.main.activity_all_review.ratingView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.writeButton
import kotlinx.android.synthetic.main.activity_review_contents.*
import kotlinx.android.synthetic.main.reservation_item_view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener {
    enum class ThumbState {
        DECRLIKE, INCRLIKE, DECRHATE, INCRHATE
    }
    var isLike: Boolean = false
    var ishate: Boolean = false
    lateinit var adapter: ReviewAdapter
    lateinit var adapter2: ReservationAdapter
    lateinit var mDrawerLayout: DrawerLayout
    var items = ArrayList<ReviewItem>()
    var mId: Int? = null
    var grade: Int? = 0
    var imageList = ArrayList<GalleryItem>()
    var movieList = ArrayList<ReservationItem>()
    var message: String? = null
    var title: String? = null
    var isAdult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 영화정보 가져오기
        val intent = intent
        movieInfoIntent(intent)

        // 툴바, 내비게이션 뷰
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

        mDrawerLayout = findViewById(R.id.drawer_layout1)

        val navigationView: NavigationView = findViewById(R.id.nav_view1)
        navigationView.setNavigationItemSelectedListener {
            it.setChecked(true)
            mDrawerLayout.closeDrawers()

            val id = it.itemId
            when(id) {
                R.id.movieList -> {
                    val intent = Intent(applicationContext, MainMovieActivity::class.java)
                    startActivity(intent)
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

        // 갤러리, 리사이클러 뷰
        val layoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = GalleryAdapter(applicationContext)
        adapter.addItems(imageList)
        recyclerView.adapter = adapter

        // 리뷰, 리스트뷰
        if(AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(applicationContext)
        }
        requestCommentList()

        // 예매
        if(AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(applicationContext)
        }
        requestMovieList()

        val layoutManger2 = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        recyclerView_reservation.layoutManager = layoutManger2

        adapter2 = ReservationAdapter(applicationContext)
        adapter2.addItems(movieList)
        recyclerView_reservation.adapter = adapter2

        // 예매버튼
        reservation_button.setOnClickListener {
            title = adapter2.getMovieTitle()
            showMessage()
        }

        dateSelect.setOnClickListener {
            val dialogFragment = DatePickerFragment()
            dialogFragment.show(supportFragmentManager, "datePicker")
        }

        // 인원 선택
        adult_button.setOnClickListener {
            isAdult = true
            showNumberPicker()
        }

        youth_button.setOnClickListener {
            if(adapter2.getMovieGrade() != 19) {
                isAdult = false
                showNumberPicker()
            } else {
                Toast.makeText(applicationContext, "해당 영화는 청소년 관람 불가 영화 입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 좋아요, 싫어요 버튼
        likeButton.setOnClickListener {
            if(isLike){
                stateCount(ThumbState.DECRLIKE)
            } else {
                stateCount(ThumbState.INCRLIKE)
            }
            isLike = !isLike
        }

        hateButton.setOnClickListener {
            if(ishate){
                stateCount(ThumbState.DECRHATE)
            } else {
                stateCount(ThumbState.INCRHATE)
            }
            ishate = !ishate
        }

        writeButton.setOnClickListener {
            val intent = Intent(applicationContext, ReviewContentsActivity::class.java)

            when(grade) {
                12 -> {
                    intent.putExtra("movieRating", R.drawable.ic_12)
                }
                15 -> {
                    intent.putExtra("movieRating", R.drawable.ic_15)
                }
                19 -> {
                    intent.putExtra("movieRating", R.drawable.ic_19)
                }
            }
            intent.putExtra("movieName", moviewName.text.toString())
            intent.putExtra("mId", mId)
            startActivityForResult(intent, 100)
        }

        // 리뷰 모두보기 버튼
        allViewButton.setOnClickListener {
            val intent = Intent(applicationContext, AllReviewActivity::class.java)
            val rating = mainRatingBar.rating
            val movieName = moviewName.text.toString()
            val movieRating = mainRatingText.text.toString()
            var movieGrade: Int = 0

            when(grade) {
                12 -> {
                    movieGrade = R.drawable.ic_12
                }
                15 -> {
                    movieGrade = R.drawable.ic_15
                }
                19 -> {
                    movieGrade = R.drawable.ic_19
                }
            }

            val data = MainInfoData(movieName, movieGrade, rating, movieRating, mId ?: 0)
            intent.putExtra("mainData", data)
            intent.putExtra("items", items)
            startActivityForResult(intent, 102)
        }
    }

    // NumberPickerDialog 관련 메서드 시작
    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        if(isAdult)
            adult_number.text = picker?.value.toString()
        else
            youth_number.text = picker?.value.toString()
    }

    fun showNumberPicker() {
        val newFragmnet = NumberPickerDialog()
        newFragmnet.setValueChangeListener(this@MainActivity)
        newFragmnet.show(supportFragmentManager, "time picker")
    }
    // 끝

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if(intent != null ) {
            when(requestCode) {
                100 -> {
                    val data = intent.getParcelableExtra<ReviewData>("saveData")

                    if(data != null) {
                        val review = data.contents
                        val rating = data.rating
                        items.add(0, ReviewItem("Mike", 0, review, rating, 0))
                        adapter.notifyDataSetChanged()
                    }
                }
                102 -> {
                    items = intent.getParcelableArrayListExtra<ReviewItem>("items") as ArrayList<ReviewItem>
                    adapter = ReviewAdapter(this@MainActivity, items)
                    listView.adapter = adapter
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }

    fun showMessage() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("확인")
        builder.setMessage("예매 하시겠습니까?")

        builder.setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
            if(adult_number.text.toString().isEmpty() && youth_number.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "인원을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else if(title == null) {
                Toast.makeText(applicationContext, "영화를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else if(message == null) {
                Toast.makeText(applicationContext, "날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                AppHelper.openDatabase(applicationContext, "movie")
                AppHelper.createTable("reservation")
                var a_number = adult_number.text.toString()
                var y_number = youth_number.text.toString()
                if(a_number.isEmpty()) { a_number = "0" }
                if(y_number.isEmpty()) { y_number = "0" }

                AppHelper.insertReservationData(title ?: "", message?:"0000/00/00", a_number, y_number)
                sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                Toast.makeText(applicationContext, "예매 완료했습니다.", Toast.LENGTH_LONG).show()
            }
        })

        builder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->

        })

        val alertDialog = builder.create()
        alertDialog.show()
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
            }
        ){}

        request.setShouldCache(false)
        AppHelper.requestQueue?.add(request)
    }

    fun processResponse(response: String) {
        val gson = Gson()
        val movieInfo = gson.fromJson(response, MovieInfo::class.java)
        for(i in 0 until movieInfo.result.size) {
            movieList.add(
                ReservationItem(
                    movieInfo.result.get(i).image,
                    movieInfo.result.get(i).title,
                    movieInfo.result.get(i).grade
                )
            )
        }
        adapter2.notifyDataSetChanged()
    }

    fun requestCommentList() {
        var url = AppHelper.host + "/readCommentList"
        url += "?id=" + mId

        val request = object : StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                readCommentList(it)
            },
            Response.ErrorListener {
            }
        ) {}

        request.setShouldCache(false)
        AppHelper.requestQueue?.add(request)
    }

    fun readCommentList(response: String) {
        val gson = Gson()
        val commentList = gson.fromJson(response, CommentList::class.java)

        adapter = ReviewAdapter(this@MainActivity, items)
        for(i in 0 until commentList.result.size) {
            val sampleDate = commentList.result.get(i).time
            val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = sf.parse(sampleDate)

            val today = Calendar.getInstance()
            var calcuDate = (today.time.time - date.time) / (60 * 60 * 24 * 1000)

            val userId = commentList.result.get(i).writer
            val comment = commentList.result.get(i).contents
            val rating = commentList.result.get(i).rating
            val recommend = commentList.result.get(i).recommend
            items.add(ReviewItem(userId, calcuDate, comment, rating, recommend))
        }
        listView.adapter = adapter
    }



    fun movieInfoIntent(intent: Intent) {
        val data = intent.getParcelableExtra<ReadInfo>("movieRead")
        Glide.with(applicationContext)
            .load(data?.image)
            .into(detail_movie_image)

        val arr = data?.date?.split("-")
        if(arr != null){
            val date = arr[0] + "." + arr[1] + "." + arr[2] + " 개봉"
            movieDate.text = date
        }

        grade = data?.grade
        when(grade) {
            12 -> {
                movieRating.setImageResource(R.drawable.ic_12)
            }
            15 -> {
                movieRating.setImageResource(R.drawable.ic_15)
            }
            19 -> {
                movieRating.setImageResource(R.drawable.ic_19)
            }
        }
        moviewName.text = data?.title
        movieDuration.text = data?.genre + " / " + data?.duration + " 분"
        likeText.text = data?.like.toString()
        hateText.text = data?.dislike.toString()
        reservation.text = data?.reservation_grade.toString() + "위" + " " + data?.reservation_rate.toString() + "%"
        mainRatingBar.rating = data?.user_rating ?: 0.0f
        mainRatingText.text = data?.audience_rating.toString()
        movieSynopsis.text = data?.synopsis
        director.text = data?.director
        actor.text = data?.actor
        val formatter = DecimalFormat("###,###")
        movieAudience.text = formatter.format(data?.audience)

        mId = data?.id

        val photolist = data?.photos?.split(",")
        if(photolist != null) {
            for(i in 0 until photolist.size)
                imageList.add(GalleryItem(photolist.get(i), 0, null))
        }

        setImageString()
    }

    fun setImageString() {
        when(mId) {
            1 -> {
                var url = "https://www.youtube.com/watch?v=4S4w7Tw9ZT8"
                var id = url.split("=").get(1)
                var imageUrl = "https://img.youtube.com/vi/" + id + "/0.jpg"
                imageList.add(GalleryItem(imageUrl, 1, id))

                url = "https://www.youtube.com/watch?v=VJAPZ9cIbs0"
                id = url.split("=").get(1)
                imageUrl = "https://img.youtube.com/vi/" + id + "/0.jpg"
                imageList.add(GalleryItem(imageUrl, 1, id))
            }
            2 -> {
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20160923_236%2F1474624538839Xes2i_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20161021_145%2F1477032859094R6hGo_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20161223_119%2F14824723006281WIq5_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20160729_31%2F14697793252640m6Xh_JPEG%2Fmovie_image.jpg", 0, null))

                var url = "https://www.youtube.com/watch?v=Rvlv3YYWypY"
                var id = url.split("=").get(1)
                var imageUrl = "https://img.youtube.com/vi/" + id + "/0.jpg"
                imageList.add(GalleryItem(imageUrl, 1, id))

                url = "https://www.youtube.com/watch?v=MI188Kj4SD0"
                id = url.split("=").get(1)
                imageUrl = "https://img.youtube.com/vi/" + id + "/0.jpg"
                imageList.add(GalleryItem(imageUrl, 1, id))
            }
            3 -> {
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20170907_146%2F1504747417109AbJ9w_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20170907_53%2F1504747417285BruPc_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20170907_121%2F15047474175501Qptu_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20170920_72%2F1505870368770GTOoy_JPEG%2Fmovie_image.jpg", 0, null))

                var url = "https://www.youtube.com/watch?v=17u-Do4FRoc"
                var id = url.split("=").get(1)
                var imageUrl = "https://img.youtube.com/vi/" + id + "/0.jpg"
                imageList.add(GalleryItem(imageUrl, 1, id))

                url = "https://www.youtube.com/watch?v=pxR6cKkPzNo"
                id = url.split("=").get(1)
                imageUrl = "https://img.youtube.com/vi/" + id + "/0.jpg"
                imageList.add(GalleryItem(imageUrl, 1, id))
            }
            4 -> {
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20170726_98%2F15010296888830viQ1_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20171012_202%2F1507775449202BvhxN_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20171012_264%2F15077754494544qwnk_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20171012_16%2F1507775449671IcQT1_JPEG%2Fmovie_image.jpg", 0, null))

                var url = "https://www.youtube.com/watch?v=51fAkv6CEns"
                var id = url.split("=").get(1)
                var imageUrl = "https://img.youtube.com/vi/" + id + "/0.jpg"
                imageList.add(GalleryItem(imageUrl, 1, id))
            }
            5 -> {
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20170816_170%2F1502845707155ll7MA_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20170816_48%2F1502845707312YI6WK_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20170816_36%2F1502845823823dGut3_JPEG%2Fmovie_image.jpg", 0, null))
                imageList.add(GalleryItem("https://search.pstatic.net/common?quality=75&direct=true&src=https%3A%2F%2Fmovie-phinf.pstatic.net%2F20170816_68%2F1502845823413DxY45_JPEG%2Fmovie_image.jpg", 0, null))

                var url = "https://www.youtube.com/watch?v=HRkSk0-2XtQ"
                var id = url.split("=").get(1)
                var imageUrl = "https://img.youtube.com/vi/" + id + "/0.jpg"
                imageList.add(GalleryItem(imageUrl, 1, id))
            }
        }
    }

    fun processDatePickerResult(year: Int, month: Int, day: Int) {
        val monthString = month.toString()
        val dayString = day.toString()
        val yearString = year.toString()
        message = yearString + "/" + monthString + "/" + dayString

        dateText.text = message
    }

    fun stateCount(state: ThumbState) {
        when(state){
            ThumbState.DECRLIKE -> {
                val like = likeText.text.toString().toInt() - 1
                likeText.setText(like.toString())
                likeButton.setBackgroundResource(R.drawable.thumb_up)
            }
            ThumbState.DECRHATE -> {
                val hate = hateText.text.toString().toInt() - 1
                hateText.setText(hate.toString())
                hateButton.setBackgroundResource(R.drawable.thumb_down)
            }
            ThumbState.INCRLIKE -> {
                if(ishate == false) {
                    val like = likeText.text.toString().toInt() + 1
                    likeText.setText(like.toString())
                    likeButton.setBackgroundResource(R.drawable.ic_thumb_up_selected)
                } else {
                    val hate = hateText.text.toString().toInt() - 1
                    hateText.setText(hate.toString())
                    hateButton.setBackgroundResource(R.drawable.thumb_down)
                    ishate = !ishate

                    val like = likeText.text.toString().toInt() + 1
                    likeText.setText(like.toString())
                    likeButton.setBackgroundResource(R.drawable.ic_thumb_up_selected)
                }
            }
            ThumbState.INCRHATE -> {
                if(isLike == false) {
                    val hate = hateText.text.toString().toInt() + 1
                    hateText.setText(hate.toString())
                    hateButton.setBackgroundResource(R.drawable.ic_thumb_down_selected)
                } else {
                    val like = likeText.text.toString().toInt() - 1
                    likeText.setText(like.toString())
                    likeButton.setBackgroundResource(R.drawable.thumb_up)
                    isLike = !isLike

                    val hate = hateText.text.toString().toInt() + 1
                    hateText.setText(hate.toString())
                    hateButton.setBackgroundResource(R.drawable.ic_thumb_down_selected)
                }
            }
        }

    }
}