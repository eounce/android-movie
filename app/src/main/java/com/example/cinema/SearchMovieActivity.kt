package com.example.cinema

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceActivity
import android.text.Layout
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.cinema.responseData.SearchMovie
import com.example.cinema.responseData.SearchMovieList
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search_movie.*
import java.net.URI
import java.net.URL
import java.net.URLEncoder

class SearchMovieActivity : AppCompatActivity() {
    private val clientId = "IhUY8BxNxWyHuxRpyWSU"
    private val clientSecret = "YhYcWoY4Z7"
    var adapter: SearchMovieAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)

        setSupportActionBar(toolbar2)

        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)

        val imm = application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        search_text.setOnEditorActionListener { v, actionId, event ->
            when(actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val keyword = search_text.text.toString()
                    if (!keyword.isEmpty()) {
                        adapter?.deleteItems()
                        if (AppHelper.requestQueue == null) {
                            AppHelper.requestQueue = Volley.newRequestQueue(applicationContext)
                        }
                        searchMovie(keyword)
                    }
                }
            }
            search_text.clearFocus()
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            true
        }

        val layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        search_recyclerView.layoutManager = layoutManager

        adapter = SearchMovieAdapter(applicationContext)
        search_recyclerView.adapter = adapter
    }

    fun searchMovie(keyword: String) {
        val url = "https://openapi.naver.com/v1/search/movie.json?query=${keyword}"

        val request = object : StringRequest(
            Request.Method.GET,
            url,
            Response.Listener {
                searchResponse(it)
            },
            Response.ErrorListener {
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params.put("X-Naver-Client-Id", clientId)
                params.put("X-Naver-Client-Secret", clientSecret)
                return params
            }
        }

        request.setShouldCache(false)
        AppHelper.requestQueue?.add(request)
    }

    fun searchResponse(response: String) {
        val gson = Gson()
        val searchMovie = gson.fromJson(response, SearchMovieList::class.java)

        adapter?.addItems(searchMovie.items)
        adapter?.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class SearchMovieAdapter(
        val context: Context
    ):RecyclerView.Adapter<SearchMovieAdapter.ViewHolder>() {
        var items = ArrayList<SearchMovie>()

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val search_titleText: TextView
            val search_directorText: TextView
            val search_actorText: TextView
            val search_ratingText: TextView
            val search_imageView: ImageView
            val item_view: LinearLayout

            init {
                this.search_titleText = view.findViewById(R.id.search_titleText)
                this.search_directorText = view.findViewById(R.id.search_directorText)
                this.search_actorText = view.findViewById(R.id.search_actorText)
                this.search_ratingText = view.findViewById(R.id.search_ratingText)
                this.search_imageView = view.findViewById(R.id.search_imageView)
                this.item_view = view.findViewById(R.id.item_view)

                item_view.setOnClickListener {
                    val position = adapterPosition
                    val sUrl = items.get(position).link

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sUrl))
                    context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.search_movie_itemview, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(context)
                .load(items.get(position).image)
                .into(holder.search_imageView)

            var title = (items.get(position).title.replace("<b>", "")).replace("</b>", "")
            holder.search_titleText.text = title + "(" + items.get(position).pubDate + ")"

            var director = items.get(position).director.replace("|", ", ")
            holder.search_directorText.text = "감독 " + director

            var actor = items.get(position).actor.replace("|", ",")
            holder.search_actorText.text = "배우 " + actor

            holder.search_ratingText.text = items.get(position).userRating
        }

        fun addItems(items: ArrayList<SearchMovie>){
            this.items = items
        }

        fun deleteItems() {
            this.items.clear()
        }
    }
}