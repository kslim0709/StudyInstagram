package com.kslim.studyinstagram.ui.navigation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ItemDetailBinding
import com.kslim.studyinstagram.ui.navigation.CommentActivity
import com.kslim.studyinstagram.ui.navigation.model.AlarmDTO
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO

class DetailViewAdapter(private val uId: String?) :
    RecyclerView.Adapter<DetailViewAdapter.DetailViewHolder>() {

    interface DetailViewItemClickListener {
        fun onItemClick(uID: String, imageUid: String)
        fun onProfileClick(uID: String, userID: String)
    }

    var detailViewItemClickListener: DetailViewItemClickListener? = null

    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()

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
                if (detailViewItemClickListener != null) {
                    detailViewItemClickListener?.onItemClick(uId!!, contentUidList[position])
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

            //This code is when the profile image is click
            detailViewBinding.ivDetailProfile.setOnClickListener {
                if (detailViewItemClickListener != null) {
                    detailViewItemClickListener?.onProfileClick(data.uId!!, data.userId!!)
                }
            }

            detailViewBinding.ivDetailComment.setOnClickListener {
                var intent = Intent(it.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", data.uId)
                it.context.startActivity(intent)
            }
        }

    }

}
