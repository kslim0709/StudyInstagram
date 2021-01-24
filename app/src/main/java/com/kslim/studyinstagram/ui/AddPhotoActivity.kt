package com.kslim.studyinstagram.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.kslim.studyinstagram.R
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        // Initiate storage
        storage = FirebaseStorage.getInstance()

        // open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        // add image upload event
    }

    @SuppressLint("SimpleDateFormat")
    private fun contentUpload() {
        // Make filename

        //FileUpload
        photoUri?.let {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "Image_" + timeStamp + "_.png"

            val storageRef = storage?.reference?.child("images")?.child(imageFileName)
            storageRef?.putFile(it)?.addOnSuccessListener {
                Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
            }
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
            } else {
                // Exit the addPhotoActivity if you leave the album without selecting it
                finish()
            }
        }

    }
}