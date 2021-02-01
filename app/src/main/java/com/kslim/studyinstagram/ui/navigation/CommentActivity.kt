package com.kslim.studyinstagram.ui.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ActivityCommentBinding
import com.kslim.studyinstagram.ui.navigation.adapter.CommentAdapter
import com.kslim.studyinstagram.ui.navigation.model.AlarmDTO
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.utils.FcmPush

class CommentActivity : AppCompatActivity() {

    lateinit var commentBinding: ActivityCommentBinding
    private var contentUid: String? = null
    private var destinationUid: String? = null

    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")

        commentBinding = DataBindingUtil.setContentView(this, R.layout.activity_comment)
        commentBinding.commentActivity = this@CommentActivity

        commentAdapter = CommentAdapter(contentUid!!)
        commentRecyclerView = commentBinding.recyComment
        commentRecyclerView.adapter = commentAdapter
        commentRecyclerView.layoutManager = LinearLayoutManager(this)

    }


    fun sendComment() {
        val comment = ContentDTO.Comment()
        comment.userId = FirebaseAuth.getInstance().currentUser?.email
        comment.uId = FirebaseAuth.getInstance().currentUser?.uid
        comment.comment = commentBinding.etCommentEdit.text.toString()
        comment.timeStamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("images").document(contentUid!!)
            .collection("comments").document().set(comment)
        commentAlarm(destinationUid!!, commentBinding.etCommentEdit.text.toString())
        commentBinding.etCommentEdit.setText("")
    }

    private fun commentAlarm(destinationUid: String, message: String) {
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.kind = 1
        alarmDTO.uId = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.timeStamp = System.currentTimeMillis()
        alarmDTO.message = message
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var msg =
            FirebaseAuth.getInstance().currentUser?.email + " " + getString(R.string.alarm_comment) + " of " + message
        FcmPush.instance.sendMessage(destinationUid, getString(R.string.app_name), msg)
    }
}