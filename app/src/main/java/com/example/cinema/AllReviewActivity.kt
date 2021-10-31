package com.example.cinema

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_all_review.*

class AllReviewActivity : AppCompatActivity() {
    lateinit var items: ArrayList<ReviewItem>
    lateinit var adapter: ReviewAdapter
    var mId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_review)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar2)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)

        val intent = intent
        mainProcessIntent(intent)

        writeButton.setOnClickListener {
            val writeIntent = Intent(applicationContext, ReviewContentsActivity::class.java)
            writeIntent.putExtra("mId", mId)
            writeIntent.putExtra("movieName", movieName.text.toString())
            writeIntent.putExtra("movieRating", R.drawable.ic_15)
            startActivityForResult(writeIntent, 101)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                intent.putExtra("items", items)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if(intent != null && requestCode == 101) {
            val data = intent.getParcelableExtra<ReviewData>("saveData")

            if(data != null) {
                val review = data.contents
                val rating = data.rating
                items.add(0, ReviewItem("Mike", 0, review, rating, 0))
                joinPerson.setText("(${items.size}명 참여)")
                adapter.notifyDataSetChanged()
            }
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }

    fun mainProcessIntent(intent: Intent) {
        val data = intent.getParcelableExtra<MainInfoData>("mainData")
        val name = data?.movieName
        val movieGrade = data?.movieGrade
        val rating = data?.ratingBar
        val movieRating = data?.movieRating
        mId = data?.mId

        movieName.setText(name)
        ratingView.setImageResource(movieGrade ?: 0)
        ratingBar.rating = rating ?: 0f
        ratingText.text = movieRating

        items = intent.getParcelableArrayListExtra<ReviewItem>("items") as ArrayList<ReviewItem>
        adapter = ReviewAdapter(this@AllReviewActivity, items)
        allListView.adapter = adapter

        joinPerson.setText("(${items.size}명 참여)")
    }
}