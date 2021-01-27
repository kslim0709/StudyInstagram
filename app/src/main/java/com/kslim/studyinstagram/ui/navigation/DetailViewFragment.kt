package com.kslim.studyinstagram.ui.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.ViewModelProviderFactory
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.databinding.FragmentDetailBinding
import com.kslim.studyinstagram.ui.navigation.adapter.DetailViewAdapter
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.ui.navigation.viewmodel.DetailViewModel

class DetailViewFragment : Fragment(), DetailViewAdapter.DetailViewItemClickListener {

    private lateinit var detailViewDataBinding: FragmentDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    private lateinit var detailViewAdapter: DetailViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        detailViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.fragment_detail,
            container,
            false
        )
        detailViewDataBinding.detailFragment = this@DetailViewFragment


        val provider =
            ViewModelProviderFactory(UserRepository.getInstance())
        detailViewModel = ViewModelProvider(this, provider).get(DetailViewModel::class.java)

        return detailViewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = detailViewDataBinding.recyDetail
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        detailViewAdapter = DetailViewAdapter(UserRepository.getInstance().currentUser()?.uid)
        detailViewAdapter.detailViewItemClickListener = this@DetailViewFragment
        recyclerView.adapter = detailViewAdapter



        detailViewModel.requestFirebaseStoreItemList()

        detailViewModel.getContentDTOList().observe(this, {
            detailViewAdapter.contentDTOs = it as ArrayList<ContentDTO>
            detailViewAdapter.notifyDataSetChanged()

        })
    }

    override fun onItemClick(uID: String, imageUid: String) {
        detailViewModel.updateFavoriteEvent(uID, imageUid)
    }

    override fun onProfileClick(uID: String, userID: String) {
        val fragment = UserFragment()
        val bundle = Bundle()
        bundle.putString("destinationUid", uID)
        bundle.putString("userId", userID)
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.main_content, fragment)?.commit()
    }

}