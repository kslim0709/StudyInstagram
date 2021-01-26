package com.kslim.studyinstagram.ui.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.ui.MainActivity
import com.kslim.studyinstagram.ui.login.LoginActivity
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO

class UserFragment : Fragment() {
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)

        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        currentUserId = auth?.currentUser?.uid

        if (uid == currentUserId) {
            // MyPage
            fragmentView?.findViewById<Button>(R.id.btn_account_follow_signout)?.text =
                getString(R.string.signout)
            fragmentView?.findViewById<Button>(R.id.btn_account_follow_signout)
                ?.setOnClickListener {
                    activity?.finish()
                    startActivity(Intent(activity, LoginActivity::class.java))
                    auth?.signOut()
                }
        } else {
            // Other User Page
            fragmentView?.findViewById<Button>(R.id.btn_account_follow_signout)?.text =
                getString(R.string.follow)
            var mainActivity = (activity as MainActivity)
            mainActivity.findViewById<TextView>(R.id.toolbar_user_name).text =
                arguments?.getString("userId")
            mainActivity.findViewById<Button>(R.id.toolbar_btn_back).setOnClickListener {
                mainActivity.findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId =
                    R.id.action_home

            }
            mainActivity.findViewById<ImageView>(R.id.toolbar_title_image).visibility = View.GONE
            mainActivity.findViewById<TextView>(R.id.toolbar_user_name).visibility = View.VISIBLE
            mainActivity.findViewById<Button>(R.id.toolbar_btn_back).visibility = View.VISIBLE
        }

        val recyclerView = fragmentView?.findViewById<RecyclerView>(R.id.recy_account)
        recyclerView?.adapter = UserFragmentRecyclerViewAdapter()
        recyclerView?.layoutManager = GridLayoutManager(activity, 3)
        return fragmentView
    }


    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTO: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("images")?.whereEqualTo("uid", uid)
                ?.addSnapshotListener { querySnapShot, firebaseFirestoreException ->
                    // Sometimes, This cod return null of querySnapshot when it signout
                    if (querySnapShot == null) return@addSnapshotListener

                    //Get Data
                    for (snapshot in querySnapShot.documents) {
                        contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    fragmentView?.findViewById<TextView>(R.id.tv_account_post_count)?.text =
                        contentDTO.size.toString()
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageView = (holder as CustomViewHolder).imageView

            Glide.with(holder.imageView.context).load(contentDTO[position].imageUrl)
                .apply(RequestOptions().centerCrop()).into(imageView)
        }

        override fun getItemCount(): Int {
            return contentDTO.size
        }

    }
}