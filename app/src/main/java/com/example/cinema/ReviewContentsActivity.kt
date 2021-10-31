package com.example.cinema

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_review_contents.*

class ReviewContentsActivity : AppCompatActivity() {
    var mId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_contents)

        val intent = intent
        processIntent(intent)

        saveButton.setOnClickListener {
            if(AppHelper == null){
                AppHelper.requestQueue = Volley.newRequestQueue(applicationContext)
            }
            requestSave()
            saveIntent()
        }

        cancelButton.setOnClickListener {
            finish()
            Toast.makeText(applicationContext, "저장을 취소 했습니다.", Toast.LENGTH_LONG).show()
        }
    }

    fun requestSave() {
        var url = AppHelper.host + "/createComment?"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            Response.Listener {

            },
            Response.ErrorListener {

            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params.put("id", mId.toString())
                params.put("writer", "Mike")
                params.put("rating", ratingBar.rating.toString())
                params.put("contents", contentsEditText.text.toString())
                return params
            }
        }

        request.setShouldCache(false)
        AppHelper.requestQueue?.add(request)
        Toast.makeText(applicationContext,"성공적으로 저장했습니다.!!",Toast.LENGTH_LONG).show()
    }

    fun processIntent(intent: Intent) {
        if(intent != null ){
            val name = intent.getStringExtra("movieName")
            val rating = intent.getIntExtra("movieRating", 0)
            mId = intent.getIntExtra("mId", 0)
            movieName.setText(name)
            ratingView.setImageResource(rating)
        }
    }

    fun saveIntent(){
        val reviewitem = ReviewData(
            contentsEditText.text.toString(),
            ratingBar.rating
        )

        val intent = Intent()
        intent.putExtra("saveData", reviewitem)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}