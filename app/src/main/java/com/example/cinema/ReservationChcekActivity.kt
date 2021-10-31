package com.example.cinema

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_reservation_chcek.*
import kotlinx.android.synthetic.main.activity_search_movie.*
import kotlinx.android.synthetic.main.reservation_check_item_view.*

class ReservationChcekActivity : AppCompatActivity() {
    val itemsList = ArrayList<ReservationItem>()
    var adapter: ReservationRecyclerViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_chcek)

        setSupportActionBar(toolbar3)

        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)

        AppHelper.openDatabase(applicationContext, "movie")
        val cursor = AppHelper.selectData("reservation", applicationContext, 0)
        if(cursor != null) {
            for (i in 0 until cursor.count) {
                cursor.moveToNext()
                var title = cursor.getString(cursor.getColumnIndex("title"))
                var reservation_date = cursor.getString(cursor.getColumnIndex("reservation_date"))
                var adult_number = cursor.getString(cursor.getColumnIndex("adult_number"))
                var youth_number = cursor.getString(cursor.getColumnIndex("youth_number"))
                var id = cursor.getInt(cursor.getColumnIndex("_id"))

                itemsList.add(ReservationItem(title, reservation_date, adult_number, youth_number, id))
            }
        }

        val layoutManger = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        reservation_recyclerView.layoutManager = layoutManger

        adapter = ReservationRecyclerViewAdapter(applicationContext)
        adapter?.addItems(itemsList)
        reservation_recyclerView.adapter = adapter
    }

    fun showMessage(id: Int) {
        val builder = AlertDialog.Builder(this@ReservationChcekActivity)
        builder.setTitle("확인")
        builder.setMessage("예매취소 하시겠습니까?")

        builder.setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
            AppHelper.deleteDate("reservation", id)
            adapter?.notifyDataSetChanged()
            Toast.makeText(applicationContext, "예매취소 했습니다.", Toast.LENGTH_LONG).show()
        })

        builder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(applicationContext, "취소", Toast.LENGTH_LONG).show()
        })

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class ReservationRecyclerViewAdapter(
        val context: Context
    ): RecyclerView.Adapter<ReservationRecyclerViewAdapter.viewHolder>() {
        var items = ArrayList<ReservationItem>()

        inner class viewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val reservation_date: TextView
            val reservation_title: TextView
            val adult_number_text: TextView
            val youth_number_text: TextView
            val cancel_button: Button

            init {
                reservation_date = view.findViewById(R.id.reservation_date)
                reservation_title = view.findViewById(R.id.reservation_title)
                adult_number_text = view.findViewById(R.id.adult_number_text)
                youth_number_text = view.findViewById(R.id.youth_number_text)
                cancel_button = view.findViewById(R.id.cancel_button)

                cancel_button.setOnClickListener {
                    val position = adapterPosition
                    showMessage(items.get(position).id)
                    items.removeAt(position)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.reservation_check_item_view, parent, false)
            return viewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: viewHolder, position: Int) {
            holder.reservation_date.text = items.get(position).date
            holder.reservation_title.text = items.get(position).movieTitle
            holder.adult_number_text.text = items.get(position).adult_number
            holder.youth_number_text.text = items.get(position).youth_number
        }

        fun addItem(item: ReservationItem){
            items.add(item)
        }

        fun addItems(items: ArrayList<ReservationItem>){
            this.items = items
        }
    }

    class ReservationItem(
       val movieTitle: String,
       val date: String,
       val adult_number: String,
       val youth_number: String,
       val id: Int
    ) {}
}