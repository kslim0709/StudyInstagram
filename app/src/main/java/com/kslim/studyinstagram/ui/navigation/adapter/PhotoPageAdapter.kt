package com.kslim.studyinstagram.ui.navigation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.databinding.ItemPhotoBinding
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import uk.co.senab.photoview.PhotoView

class PhotoPageAdapter() :
    PagerAdapter() {

    private var mPhotoInfoList: ArrayList<ContentDTO>? = null
    private var mPhotoPageListener: PhotoPageListener? = null


    fun setPhotoInfoList(photoInfoList: ArrayList<ContentDTO>) {
        this.mPhotoInfoList = photoInfoList
    }

    fun setPhotoPageListener(photoPageListener: PhotoPageListener) {
        this.mPhotoPageListener = photoPageListener
    }

    override fun getCount(): Int {
        return mPhotoInfoList?.let { mPhotoInfoList!!.size } ?: 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.item_photo,
            container,
            false
        ) as ItemPhotoBinding


        val photoView: PhotoView = binding.ivPhoto
        photoView.setOnViewTapListener { view, x, y ->
            mPhotoPageListener?.toggleImmersiveMode()
        }

        Glide.with(binding.root).load(mPhotoInfoList?.get(position)?.imageUrl)
            .apply(RequestOptions().fitCenter())
            .into(binding.ivPhoto)
        container.addView(binding.root)

        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view: View = `object` as View
        container.removeView(view)

    }

    interface PhotoPageListener {
        fun toggleImmersiveMode()
    }

}