package com.kslim.studyinstagram.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import bolts.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ActivityAddPhotoBinding
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    private lateinit var addPhotoBinding: ActivityAddPhotoBinding


    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null

    var auth: FirebaseAuth? = null
    var fireStore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        addPhotoBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo)
        addPhotoBinding.addPhotoActivity = this@AddPhotoActivity

        // Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        // open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        // add image upload event
    }

    @SuppressLint("SimpleDateFormat")
    fun contentUpload() {
        // Make filename

        //FileUpload
        photoUri?.let {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "Image_" + timeStamp + "_.png"

            val storageRef = storage?.reference?.child("images")?.child(imageFileName)

            // Promise method  ( 권장사항 )
            storageRef?.putFile(it)
                ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storageRef.downloadUrl
                }?.addOnSuccessListener { uri ->
                    val contentDTO = ContentDTO()
                    // Insert downloadUrl of image
                    contentDTO.imageUrl = uri.toString()

                    // Insert uid of user
                    contentDTO.uId = auth?.currentUser?.uid

                    // Insert userId
                    contentDTO.userId = auth?.currentUser?.email

                    // Insert explain of content
                    contentDTO.explain = addPhotoBinding.addphotoEditExplain.text.toString()

                    //Insert TimeStamp
                    contentDTO.timeStamp = System.currentTimeMillis()

                    fireStore?.collection("images")?.document()?.set(contentDTO)
                    setResult(Activity.RESULT_OK)

                    finish()
                }

            // Callback method
//            storageRef?.putFile(it)?.addOnSuccessListener {
//                Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
//                storageRef.downloadUrl.addOnCompleteListener { uri ->
//                    var contentDTO = ContentDTO()
//                    // Insert downloadUrl of image
//                    contentDTO.imageUrl = uri.toString()
//
//                    // Insert uid of user
//                    contentDTO.uId = auth?.currentUser?.uid
//
//                    // Insert userId
//                    contentDTO.userId = auth?.currentUser.email
//
//                    // Insert explain of content
//                    contentDTO.explain = addPhotoBinding.addphotoEditExplain.text.toString()
//
//                    //Insert TimeStamp
//                    contentDTO.timeStamp = System.currentTimeMillis()
//
//                    fireStore?.collection("images")?.document()?.set(contentDTO)
//                    setResult(Activity.RESULT_OK)
//
//                    finish()
//                }
//            }
        } ?: run {
            Toast.makeText(this, getString(R.string.upload_fail), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                // This is path to the selected image
                photoUri = data?.data
                addPhotoBinding.addphotoImage.setImageURI(photoUri)
            } else {
                // Exit the addPhotoActivity if you leave the album without selecting it
                finish()
            }
        }

    }
}