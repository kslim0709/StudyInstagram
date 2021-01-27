package com.kslim.studyinstagram.ui.navigation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ItemDetailBinding
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO

class DetailViewAdapter(private val uId: String?) :
    RecyclerView.Adapter<DetailViewAdapter.DetailViewHolder>() {

    interface DetailViewItemClickListener {
        fun onItemClick(uID: String)
    }

    var detailViewItemClickListener: DetailViewItemClickListener? = null

    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
    private var contentUidList: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_detail,
            parent,
            false
        ) as ItemDetailBinding
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.onBind(contentDTOs[position], position)
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }

    inner class DetailViewHolder(private val detailViewBinding: ItemDetailBinding) :
        RecyclerView.ViewHolder(detailViewBinding.root) {

        fun onBind(data: ContentDTO, position: Int) {
            // UserID
            detailViewBinding.tvDetailProfile.text = data.userId

            // Image
            Glide.with(detailViewBinding.root.context).load(data.imageUrl)
                .into(detailViewBinding.ivDetailContent)

            // Explain of Content
            detailViewBinding.tvDetailExplain.text = data.explain

            // Likes
            detailViewBinding.tvDetailFavoriteCount.text = "Likes " + data.favoriteCount

            // ProfileImage
            Glide.with(detailViewBinding.root.context).load(data.imageUrl)
                .into(detailViewBinding.ivDetailProfile)

            // This code is when the button is clicked
            detailViewBinding.ivDetailFavorite.setOnClickListener {
                Log.v("DetailFragment", "Adapter Onclick")
                if (detailViewItemClickListener != null) {
                    val uId = contentDTOs[position].uId
                    detailViewItemClickListener?.onItemClick(uId!!)
                }
            }

            // This code is when the page is loaded
            if (data.favorites.containsKey(uId)) {
                // This is like status
                detailViewBinding.ivDetailFavorite.setImageResource(R.drawable.ic_favorite)
            } else {
                // This is unlike status
                detailViewBinding.ivDetailFavorite.setImageResource(R.drawable.ic_favorite_border)
            }
        }

    }

}