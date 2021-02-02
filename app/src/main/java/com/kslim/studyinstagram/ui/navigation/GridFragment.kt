package com.kslim.studyinstagram.ui.navigation

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
import com.kslim.studyinstagram.databinding.FragmentGridBinding
import com.kslim.studyinstagram.ui.navigation.adapter.GridAdapter
import com.kslim.studyinstagram.ui.navigation.model.ContentDTO
import com.kslim.studyinstagram.ui.navigation.viewmodel.GridViewModel
import com.kslim.studyinstagram.utils.ImageOffsetDecoration

class GridFragment : Fragment() {

    private lateinit var gridDataBinding: FragmentGridBinding
    private lateinit var gridViewModel: GridViewModel

    private lateinit var gridAdapter: GridAdapter
    private lateinit var gridRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gridDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.fragment_grid,
            container,
            false
        )

        gridDataBinding.gridFragment = this@GridFragment

        val provider = ViewModelProviderFactory(UserRepository.getInstance())
        gridViewModel = ViewModelProvider(this, provider).get(GridViewModel::class.java)

        return gridDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridAdapter = GridAdapter()
        gridRecyclerView = gridDataBinding.recyGridFragment

        gridRecyclerView.adapter = gridAdapter
        gridRecyclerView.layoutManager = GridLayoutManager(view.context, 3)
        gridRecyclerView.addItemDecoration(ImageOffsetDecoration(6))

        val currentUserId = UserRepository.getInstance().currentUser()?.uid
        currentUserId?.let { gridViewModel.requestFirebaseStoreItemList(it) }


        gridViewModel.getContentDTOList().observe(this, {
            gridAdapter.contentDTOs = it as ArrayList<ContentDTO>
            gridAdapter.notifyDataSetChanged()
        })
    }
}