package com.kslim.studyinstagram.ui.navigation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ItemCommentBinding
import com.kslim.studyinstagram.ui.navigation.model.AlarmDTO

class AlarmAdapter : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {
    var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()

    init {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid)
            .addSnapshotListener { querySnapshot, error ->
                alarmDTOList.clear()

                if (querySnapshot == null) return@addSnapshotListener

                for (snapshot in querySnapshot) {
                    alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java))
                }
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_comment,
            parent,
            false
        ) as ItemCommentBinding
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.onBind(alarmDTOList[position])
    }

    override fun getItemCount(): Int {
        return alarmDTOList.size
    }


    inner class AlarmViewHolder(private val alarmBinding: ItemCommentBinding) :
        RecyclerView.ViewHolder(alarmBinding.root) {


        fun onBind(alarmDTO: AlarmDTO) {

            FirebaseFirestore.getInstance().collection("profileImages").document(alarmDTO.uId!!)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val url = task.result?.get("image")
                        Glide.with(alarmBinding.root).load(url).apply(RequestOptions().circleCrop())
                            .into(alarmBinding.ivCommentProfile)
                    }
                }

            when (alarmDTO.kind) {
                0 -> {
                    val str =
                        alarmDTO.userId + " " + alarmBinding.root.context.getString(R.string.alarm_favorite)
                    alarmBinding.tvCommentProfile.text = str
                }
                1 -> {
                    val str =
                        alarmDTO.userId + " " + alarmBinding.root.context.getString(R.string.alarm_comment) + " of " + alarmDTO.message
                    alarmBinding.tvCommentProfile.text = str
                }
                2 -> {
                    val str =
                        alarmDTO.userId + " " + alarmBinding.root.context.getString(R.string.alarm_follow)
                    alarmBinding.tvCommentProfile.text = str
                }
            }

            alarmBinding.tvComment.visibility = View.INVISIBLE

        }

    }
}