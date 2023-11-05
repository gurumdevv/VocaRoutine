package com.gurumlab.vocaroutine.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import com.gurumlab.vocaroutine.VocaRoutineApplication
import com.gurumlab.vocaroutine.data.model.MyList
import com.gurumlab.vocaroutine.databinding.FragmentMyListBinding
import kotlinx.coroutines.launch


class MyListFragment : BaseFragment<FragmentMyListBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyListBinding {
        return FragmentMyListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myListAdapter = MyListAdapter(object : ListClickListener {
            override fun onClick(list: MyList) {
                val action = MyListFragmentDirections.actionMyListToDetail(list)
                findNavController().navigate(action)
            }
        })

        val apiClient = VocaRoutineApplication.appContainer.provideApiClient()

        binding!!.rvMyList.adapter = myListAdapter

        lifecycleScope.launch {
            val myLists = apiClient.getLists()
            myListAdapter.submitList(myLists)
        }
    }
}