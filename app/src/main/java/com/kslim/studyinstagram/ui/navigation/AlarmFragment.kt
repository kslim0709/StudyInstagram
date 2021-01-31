package com.kslim.studyinstagram.ui.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kslim.studyinstagram.R
import com.kslim.studyinstagram.ViewModelProviderFactory
import com.kslim.studyinstagram.data.repository.UserRepository
import com.kslim.studyinstagram.databinding.FragmentAlarmBinding
import com.kslim.studyinstagram.databinding.FragmentDetailBinding
import com.kslim.studyinstagram.ui.navigation.adapter.AlarmAdapter
import com.kslim.studyinstagram.ui.navigation.adapter.DetailViewAdapter
import com.kslim.studyinstagram.ui.navigation.adapter.GridAdapter
import com.kslim.studyinstagram.ui.navigation.viewmodel.DetailViewModel
import com.kslim.studyinstagram.ui.navigation.viewmodel.GridViewModel

class AlarmFragment : Fragment() {

    private lateinit var alarmDataBinding: FragmentAlarmBinding
//    private lateinit var alarmViewModel: DetailViewModel

    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        alarmDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.fragment_alarm,
            container,
            false
        )

        alarmDataBinding.alarmFragment = this@AlarmFragment

//        val provider = ViewModelProviderFactory(UserRepository.getInstance())
//        gridViewModel = ViewModelProvider(this, provider).get(GridViewModel::class.java)

        return alarmDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmAdapter = AlarmAdapter()
        alarmRecyclerView = alarmDataBinding.recyAlarm

        alarmRecyclerView.adapter = alarmAdapter
        alarmRecyclerView.layoutManager = LinearLayoutManager(activity)
    }
}