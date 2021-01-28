package com.kslim.studyinstagram.ui.navigation

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.ViewModelProviderFactory
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.databinding.FragmentUserBinding
import com.kslim.studyinstagram.ui.MainActivity
import com.kslim.studyinstagram.ui.login.LoginActivity
import com.kslim.studyinstagram.ui.navigation.adapter.UserAdapter
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.ui.navigation.viewmodel.UserViewModel

class UserFragment : Fragment() {
    private var uid: String? = null
    private var currentUserId: String? = null

    private lateinit var userDataBinding: FragmentUserBinding
    private lateinit var userViewModel: UserViewModel

    private lateinit var userAdapter: UserAdapter
    private lateinit var userRecyclerView: RecyclerView

    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.fragment_user,
            container,
            false
        )
        userDataBinding.userFragment = this@UserFragment


        val provider = ViewModelProviderFactory(UserRepository.getInstance())
        userViewModel = ViewModelProvider(this, provider).get(UserViewModel::class.java)

        return userDataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = arguments?.getString("destinationUid")
        currentUserId = UserRepository.getInstance().currentUser()?.uid
        Log.v("UserFragment", "viewCreate uid: ${uid},, currentUserId: ${currentUserId}")
        if (uid == currentUserId) {
            // MyPage
            userDataBinding.btnAccountFollowSignout.text = getString(R.string.signout)
        } else {
            // Other User Page
            userDataBinding.btnAccountFollowSignout.text = getString(R.string.follow)

            val mainActivity = (activity as MainActivity)
            mainActivity.mainBinding.toolbarUserName.text = arguments?.getString("userId")
            mainActivity.mainBinding.toolbarBtnBack.setOnClickListener {
                mainActivity.mainBinding.bottomNavigation.selectedItemId = R.id.action_home
            }

            mainActivity.mainBinding.toolbarTitleImage.visibility = View.GONE
            mainActivity.mainBinding.toolbarUserName.visibility = View.VISIBLE
            mainActivity.mainBinding.toolbarBtnBack.visibility = View.VISIBLE

        }

        userAdapter = UserAdapter()
        userRecyclerView = userDataBinding.recyAccount
        userRecyclerView.adapter = userAdapter
        userRecyclerView.layoutManager = GridLayoutManager(activity, 3)


        uid?.let { userViewModel.requestFirebaseStoreItemList(it) }

        uid?.let { userViewModel.getFirebaseStoreProfileImage(it) }

        uid?.let { userViewModel.getFollowerAndroidFollowing(it) }

        userViewModel.getContentDTOList().observe(this, {
            userAdapter.contentDTOs = it as ArrayList<ContentDTO>
            userAdapter.notifyDataSetChanged()
        })

        userViewModel.getFirebaseStoreProfileImageURL().observe(this, {
            if (it != null) {
                Glide.with(view.context).load(it).apply(RequestOptions().circleCrop())
                    .into(userDataBinding.ivAccountProfile)
            }
        })

        userViewModel.getFollowerAndroidFollowingData().observe(this, {

            userDataBinding.tvAccountFollowingCount.text = it.followingCount.toString()
            userDataBinding.tvAccountFollowerCount.text = it.followerCount.toString()

            if (it.followers.containsKey(currentUserId)) {
                userDataBinding.btnAccountFollowSignout.text = getString(R.string.follow_cancel)
                userDataBinding.btnAccountFollowSignout.background.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(
                            view.context,
                            R.color.colorLightGray
                        ), PorterDuff.Mode.MULTIPLY
                    )
            } else {
                if (uid != currentUserId) {
                    userDataBinding.btnAccountFollowSignout.text = getString(R.string.follow)
                    userDataBinding.btnAccountFollowSignout.background.colorFilter = null
                }
            }
        })

    }

    fun setUserProfile() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
    }

    fun btnAccountFollowSignOut() {
        if (uid == currentUserId) {
            activity?.finish()
            startActivity(Intent(activity, LoginActivity::class.java))
            UserRepository.getInstance().logout()
        } else {
            userViewModel.requestFollow(uid!!, currentUserId!!)
        }
    }
}