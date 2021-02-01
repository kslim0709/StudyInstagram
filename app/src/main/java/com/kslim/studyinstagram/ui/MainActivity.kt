package com.kslim.studyinstagram.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ActivityMainBinding
import com.kslim.studyinstagram.ui.navigation.AlarmFragment
import com.kslim.studyinstagram.ui.navigation.DetailViewFragment
import com.kslim.studyinstagram.ui.navigation.GridFragment
import com.kslim.studyinstagram.ui.navigation.UserFragment
import com.kslim.studyinstagram.ui.photo.AddPhotoActivity

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var mainBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.mainActivity = this@MainActivity

        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener(this)


        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        //Set default screen
        mainBinding.bottomNavigation.selectedItemId = R.id.action_home
        registerPushToken()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setToolbarDefault()
        Log.v("main", "onNaviGation: " + item.itemId)
        when (item.itemId) {
            R.id.action_home -> {
                val detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, detailViewFragment).commit()
                return true
            }
            R.id.action_search -> {
                val gridFragment = GridFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, gridFragment).commit()
                return true
            }
            R.id.action_add_photo -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(Intent(this, AddPhotoActivity::class.java))
                }
                return true
            }
            R.id.action_favorite_alarm -> {
                val alarmFragment = AlarmFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, alarmFragment).commit()
                return true
            }
            R.id.action_account -> {
                val userFragment = UserFragment()
                val bundle = Bundle()
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                bundle.putString("destinationUid", uid)
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, userFragment).commit()
                return true
            }
        }
        return false
    }

    private fun setToolbarDefault() {
        mainBinding.toolbarUserName.visibility = View.GONE
        mainBinding.toolbarBtnBack.visibility = View.GONE
        mainBinding.toolbarTitleImage.visibility = View.VISIBLE
    }

    private fun registerPushToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val token = task.result
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String, Any>()
            map["pushToken"] = token!!

            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            val imageUrl = data?.data
            val uid = FirebaseAuth.getInstance().currentUser?.uid
//            val storeageRef =
//                FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
//            storeageRef.putFile(imageUrl!!)
//                .continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
//                    return@continueWithTask storeageRef.downloadUrl
//                }.addOnSuccessListener { uri ->
//                var map = HashMap<String, Any>()
//                map["image"] = uri.toString()
//                FirebaseStorage.getInstance().collection("profileImages").document(uid).set(map)

            val storeageRef =
                FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)

            storeageRef.putFile(imageUrl!!)
                .continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storeageRef.downloadUrl
                }.addOnSuccessListener { uri ->
                    val map = HashMap<String, Any>()
                    map["image"] = uri.toString()
                    FirebaseFirestore.getInstance().collection("profileImages").document(uid)
                        .set(map)

                }
        }
    }
}