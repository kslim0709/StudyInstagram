package com.kslim.studyinstagram.ui.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ActivityPhotoPageBinding
import com.kslim.studyinstagram.ui.navigation.adapter.PhotoPageAdapter
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO

class PhotoPageActivity : AppCompatActivity(), PhotoPageAdapter.PhotoPageListener {

    private lateinit var mBinding: ActivityPhotoPageBinding
    private lateinit var mPhotoPageAdapter: PhotoPageAdapter

    private lateinit var mPhotoViewPager: ViewPager
    private lateinit var mPhotoPageChangeListener: PhotoPageChangeListener

    private var mPhotoInfoIndex: Int = -1
    private var mPhotoInfoList: ArrayList<ContentDTO>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_page)
        mBinding.photoPageActivity = this@PhotoPageActivity

        mPhotoInfoIndex = intent.getIntExtra("photoInfoIndex", -1)
        mPhotoInfoList = intent.getSerializableExtra("photoInfoList") as ArrayList<ContentDTO>

        window.decorView.setOnSystemUiVisibilityChangeListener {
            if (View.SYSTEM_UI_FLAG_VISIBLE == it) {
                mBinding.headerView.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@PhotoPageActivity,
                        R.anim.top_enter_anim
                    )
                )
            } else {
                mBinding.headerView.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@PhotoPageActivity,
                        R.anim.top_exit_anim
                    )
                )
            }
        }

        initView()

    }

    private fun initView() {

        mPhotoViewPager = mBinding.photoViewPager

        mPhotoPageAdapter = PhotoPageAdapter()
        mPhotoPageAdapter.setPhotoInfoList(mPhotoInfoList!!)
        mPhotoPageAdapter.setPhotoPageListener(this)

        mPhotoViewPager.adapter = mPhotoPageAdapter

        mPhotoPageChangeListener = PhotoPageChangeListener()
        mPhotoViewPager.addOnPageChangeListener(mPhotoPageChangeListener)

        mBinding.ivBack.setOnClickListener { finish() }

        if (mPhotoInfoIndex != -1 && mPhotoInfoList != null) {
            mPhotoViewPager.currentItem = mPhotoInfoIndex
            setPositionToTitle(mPhotoInfoIndex)
        }
    }

    private inner class PhotoPageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            setPositionToTitle(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun setPositionToTitle(position: Int) {
        if (mPhotoInfoList != null) {
            val title = String.format(
                getString(R.string.image_index),
                position + 1,
                mPhotoInfoList!!.size
            )
            mBinding.tvTitle.text = title
        }
    }

    override fun toggleImmersiveMode() {
        var uiOptions = window.decorView.systemUiVisibility
        // Navigation bar hiding:  Backwards compatible to ICS.
        uiOptions = uiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        // Status bar hiding: Backwards compatible to Jellybean
        uiOptions = uiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        // Immersive mode: Backward compatible to KitKat.
        uiOptions = uiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = uiOptions
    }
}