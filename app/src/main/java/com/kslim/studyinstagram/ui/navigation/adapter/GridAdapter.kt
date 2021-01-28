package com.kslim.studyinstagram.ui.navigation.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO

class GridAdapter : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {
    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val width = parent.context.resources.displayMetrics.widthPixels / 3

        val imageView = ImageView(parent.context)
        imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
        return GridViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.onBind(holder, position)
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }


    inner class GridViewHolder(private var imageView: ImageView) :
        RecyclerView.ViewHolder(imageView) {


        fun onBind(holder: GridViewHolder, position: Int) {
            val imageView = holder.imageView

            Glide.with(holder.imageView.context).load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop()).into(imageView)
        }


    }
}