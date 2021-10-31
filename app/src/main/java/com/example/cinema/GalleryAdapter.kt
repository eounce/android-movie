package com.example.cinema

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.movie1_fragment.*

class GalleryAdapter(
    val context: Context
): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    var items = ArrayList<GalleryItem>()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        val playImageView: ImageView

        init {
            imageView = view.findViewById(R.id.imageView)
            playImageView = view.findViewById(R.id.playImage)

            imageView.setOnClickListener{
                val position = adapterPosition

                if(items.get(position).gubun == 0) {
                    val intent = Intent(context, PhotoViewActivity::class.java)
                    intent.putExtra("url", items.get(position).image)
                    context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
                } else {
                    if(items.get(position).id != null) {
                        val url = "https://www.youtube.com/watch?v=" + items.get(position).id
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.gallery_item_view, parent, false)
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

        if(items.get(position).gubun == 1){
            holder.playImageView.setImageResource(R.drawable.ic_play_32)
        }
    }

    fun addItem(item: GalleryItem) {
        items.add(item)
    }

    fun addItems(items: ArrayList<GalleryItem>) {
        this.items = items
    }
}