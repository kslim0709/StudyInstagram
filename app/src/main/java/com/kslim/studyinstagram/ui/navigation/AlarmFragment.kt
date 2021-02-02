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
import com.kslim.studyinstagram.databinding.FragmentAlarmBinding
import com.kslim.studyinstagram.ui.navigation.adapter.AlarmAdapter
import com.kslim.studyinstagram.ui.navigation.model.AlarmDTO
import com.kslim.studyinstagram.ui.navigation.viewmodel.AlarmViewModel

class AlarmFragment : Fragment() {

    private lateinit var alarmDataBinding: FragmentAlarmBinding
    private lateinit var alarmViewModel: AlarmViewModel

    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        alarmDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.fragment_alarm,
            container,
            false
        )

        alarmDataBinding.alarmFragment = this@AlarmFragment

        val provider = ViewModelProviderFactory(UserRepository.getInstance())
        alarmViewModel = ViewModelProvider(this, provider).get(AlarmViewModel::class.java)

        return alarmDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmAdapter = AlarmAdapter()
        alarmRecyclerView = alarmDataBinding.recyAlarm

        alarmRecyclerView.adapter = alarmAdapter
        alarmRecyclerView.layoutManager = LinearLayoutManager(activity)

        alarmViewModel.requestFirebaseStoreUserAlarmList()

        alarmViewModel.getAlarmDTOList().observe(this, {
            if (!it.isNullOrEmpty()) {
                alarmAdapter.alarmDTOList = it as ArrayList<AlarmDTO>
                alarmAdapter.notifyDataSetChanged()
            }
        })
    }
}