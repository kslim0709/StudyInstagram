package com.kslim.studyinstagram.ui.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO

class DetailViewFragment : Fragment() {

    var uid: String? = null
    var fireStore: FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        fireStore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        val recyclerView = view.findViewById<RecyclerView>(R.id.recy_detail_fragment)
        recyclerView.adapter = DetailViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class DetailViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            fireStore?.collection("images")?.orderBy("timeStamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()

                    for (snapShot in querySnapshot!!.documents) {
                        var item = snapShot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapShot.id)
                    }
                    notifyDataSetChanged()

                }
        }


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = (holder as CustomViewHolder).itemView

            val contentDTO = contentDTOs[position]

            // UserID
            viewHolder.findViewById<TextView>(R.id.tv_detail_profile).text =
                contentDTO.userId

            // Image
            Glide.with(holder.itemView.context).load(contentDTO.imageUrl)
                .into(viewHolder.findViewById(R.id.iv_detail_content))

            // Explain of content
            viewHolder.findViewById<TextView>(R.id.tv_detail_explain).text =
                contentDTO.explain

            //Likes
            viewHolder.findViewById<TextView>(R.id.tv_detail_favorite_count).text =
                "Likes " + contentDTO.favoriteCount

            //ProfileImage
            Glide.with(holder.itemView.context).load(contentDTO.imageUrl)
                .into(viewHolder.findViewById(R.id.iv_detail_profile))

            // This code is when the button is clicked
            viewHolder.findViewById<ImageView>(R.id.iv_detail_favorite).setOnClickListener {
                favoriteEvent(position)
            }

            // This code is when the page is loaded
            if (contentDTOs[position].favorites.containsKey(uid)) {
                // This is like status
                viewHolder.findViewById<ImageView>(R.id.iv_detail_favorite)
                    .setImageResource(R.drawable.ic_favorite)
            } else {
                // This is unlike status
                viewHolder.findViewById<ImageView>(R.id.iv_detail_favorite)
                    .setImageResource(R.drawable.ic_favorite_border)
            }

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        fun favoriteEvent(position: Int) {
            var tsDoc = fireStore?.collection("images")?.document(contentUidList[position])

            fireStore?.runTransaction { transition ->
                val contentDTO = transition.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) {
                    // When the button is clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO.favorites.remove(uid)

                } else {
                    // when the button is not clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                    contentDTO.favorites[uid!!] = true
                }

                transition.set(tsDoc, contentDTO)
            }
        }

    }
}