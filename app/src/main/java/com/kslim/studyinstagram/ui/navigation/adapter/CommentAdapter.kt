package com.kslim.studyinstagram.ui.navigation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ItemCommentBinding
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO

class CommentAdapter(private val contentUid: String) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    var comments: ArrayList<ContentDTO.Comment> = arrayListOf()

    init {
        FirebaseFirestore.getInstance().collection("images").document(contentUid)
            .collection("comments").orderBy("timeStamp").addSnapshotListener { value, error ->
                comments.clear()

                if (value == null) return@addSnapshotListener

                for (snapshot in value) {
                    comments.add(snapshot.toObject(ContentDTO.Comment::class.java))
                }
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_comment,
            parent,
            false
        ) as ItemCommentBinding


        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.onBind(comments[position])
    }

    override fun getItemCount(): Int {
        return comments.size
    }


    inner class CommentViewHolder(private val itemCommentBinding: ItemCommentBinding) :
        RecyclerView.ViewHolder(itemCommentBinding.root) {


        fun onBind(comment: ContentDTO.Comment) {

            itemCommentBinding.tvComment.text = comment.comment
            itemCommentBinding.tvCommentProfile.text = comment.userId

            FirebaseFirestore.getInstance().collection("images").document(comment.uId!!).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val url = task.result!!["image"]
                        Glide.with(itemCommentBinding.root).load(url)
                            .apply(RequestOptions().circleCrop())
                            .into(itemCommentBinding.ivCommentProfile)
                    }

                }
        }


    }
}