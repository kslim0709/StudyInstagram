package com.kslim.studyinstagram.ui.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    var uid: String? = null
    var currentUserId: String? = null

    private lateinit var userDataBinding: FragmentUserBinding
    private lateinit var userViewModel: UserViewModel

    private lateinit var userAdapter: UserAdapter
    private lateinit var userRecyclerView: RecyclerView

    private val userRepository: UserRepository = UserRepository.getInstance()


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


        val provider = ViewModelProviderFactory(userRepository)
        userViewModel = ViewModelProvider(this, provider).get(UserViewModel::class.java)

        return userDataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = arguments?.getString("destinationUid")

        currentUserId = userRepository.currentUser()?.uid

        if (uid == currentUserId) {
            // MyPage
            userDataBinding.btnAccountFollowSignout.text = getString(R.string.signout)
            userDataBinding.btnAccountFollowSignout.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity, LoginActivity::class.java))
                userRepository.logout()
            }
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


        userRepository.currentUser()?.let { userViewModel.requestFirebaseStoreItemList(it.uid) }

        userViewModel.getContentDTOList().observe(this, {
            userAdapter.contentDTOs = it as ArrayList<ContentDTO>
            userAdapter.notifyDataSetChanged()
        })
    }

}