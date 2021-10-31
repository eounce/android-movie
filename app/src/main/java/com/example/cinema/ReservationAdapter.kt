package com.example.cinema

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinema.responseData.MovieList
import kotlinx.android.synthetic.main.reservation_item_view.view.*

class ReservationAdapter(
    val context: Context
): RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {
    var items = ArrayList<ReservationItem>()
    var title: String? = null
    var grade: Int? = null
    var isCheck = false
    var oldView: View? = null

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView : ImageView
        val textView : TextView
        val check : ImageView
        val background : ImageView
        val gradeImage : ImageView
        val relativeLayout : RelativeLayout

        init {
            imageView = view.findViewById(R.id.reservation_image)
            textView = view.findViewById(R.id.reservation_text)
            check = view.findViewById(R.id.reservation_check)
            background = view.findViewById(R.id.reservation_background_image)
            gradeImage = view.findViewById(R.id.reservation_grade)
            relativeLayout = view.findViewById(R.id.reservation_relativeLayout)


            relativeLayout.setOnClickListener {
                if(oldView == it) {
                    oldView?.reservation_text?.visibility = View.INVISIBLE
                    oldView?.reservation_background_image?.visibility = View.INVISIBLE
                    oldView?.reservation_check?.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                    isCheck = false
                    oldView = null
                    title = null
                    grade = null
                } else {
                    if (!isCheck) {
                        oldView = it
                        isCheck = true
                        textView.visibility = View.VISIBLE
                        background.visibility = View.VISIBLE
                        check.setImageResource(R.drawable.ic_baseline_check_circle_24)
                        title = textView.text.toString()
                        grade = items.get(adapterPosition).grade
                    } else {
                        oldView?.reservation_text?.visibility = View.INVISIBLE
                        oldView?.reservation_background_image?.visibility = View.INVISIBLE
                        oldView?.reservation_check?.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                        oldView = it

                        textView.visibility = View.VISIBLE
                        background.visibility = View.VISIBLE
                        check.setImageResource(R.drawable.ic_baseline_check_circle_24)
                        title = textView.text.toString()
                        grade = items.get(adapterPosition).grade
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.reservation_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(items.get(position).image)
            .centerCrop()
            .into(holder.imageView)
        holder.textView.text = items.get(position).title

        when(items.get(position).grade) {
            12 -> {
                holder.gradeImage.setImageResource(R.drawable.ic_12)
            }
            15 -> {
                holder.gradeImage.setImageResource(R.drawable.ic_15)
            }
            19 -> {
                holder.gradeImage.setImageResource(R.drawable.ic_19)
            }
        }
    }

    fun addItems(items: ArrayList<ReservationItem>) {
        this.items = items
    }

    fun getMovieTitle(): String? {
        return title
    }

    fun getMovieGrade(): Int? {
        return grade
    }
}