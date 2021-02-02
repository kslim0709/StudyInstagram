package com.kslim.studyinstagram.ui.photo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.ViewModelProviderFactory
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.databinding.ActivityAddPhotoBinding
import com.kslim.studyinstagram.utils.startMainActivity
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity(), AddPhotoListener {

    private lateinit var addPhotoBinding: ActivityAddPhotoBinding
    private lateinit var addPhotoViewModel: AddPhotoViewModel

    private var photoUri: Uri? = null
    private var PICK_IMAGE_FROM_ALBUM = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        addPhotoBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo)
        addPhotoBinding.addPhotoActivity = this@AddPhotoActivity

        val provider = ViewModelProviderFactory(UserRepository.getInstance())
        addPhotoViewModel = ViewModelProvider(this, provider).get(AddPhotoViewModel::class.java)
        addPhotoViewModel.addPhotoListener = this@AddPhotoActivity

        // open the album
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
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
            val photoName = "Image_" + timeStamp + "_.png"
            val photoExplain = addPhotoBinding.addphotoEditExplain.text.toString()
            addPhotoViewModel.contentUpload(it, photoName, photoExplain)

        } ?: run {
            onFailUpload(Throwable(getString(R.string.upload_fail)))
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

    override fun onSuccessUpload() {
        startMainActivity()
        finish()
    }

    override fun onFailUpload(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
    }
}