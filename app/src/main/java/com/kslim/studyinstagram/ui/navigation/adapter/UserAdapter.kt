package com.kslim.studyinstagram.ui.navigation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ItemGridBinding
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_grid,
            parent,
            false
        ) as ItemGridBinding
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.onBind(holder, position)
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }


    inner class UserViewHolder(private var itemGridBinding: ItemGridBinding) :
        RecyclerView.ViewHolder(itemGridBinding.root) {


        fun onBind(holder: UserViewHolder, position: Int) {
            val imageView = holder.itemGridBinding.ivPhoto

            Glide.with(holder.itemGridBinding.root).load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop()).into(imageView)
        }


    }
}