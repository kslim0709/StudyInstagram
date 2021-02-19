package com.kslim.studyinstagram.ui.navigation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ItemGridBinding
import com.kslim.studyinstagram.ui.navigation.PhotoPageActivity
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO

class GridAdapter : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {
    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_grid,
            parent,
            false
        ) as ItemGridBinding


        return GridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.onBind(holder, position)
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }


    inner class GridViewHolder(private var gridBinding: ItemGridBinding) :
        RecyclerView.ViewHolder(gridBinding.root) {


        fun onBind(holder: GridViewHolder, position: Int) {
            val imageView = holder.gridBinding.ivPhoto

            Glide.with(gridBinding.root).load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop()).into(imageView)


            holder.gridBinding.itemImage.setOnClickListener {
                val intent = Intent(it.context, PhotoPageActivity::class.java)
                intent.putExtra("photoInfoIndex", position)
                intent.putExtra("photoInfoList", contentDTOs)
                it.context.startActivity(intent)
            }
        }


    }
}