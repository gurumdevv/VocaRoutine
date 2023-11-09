package com.gurumlab.vocaroutine.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.databinding.FragmentMyListBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyListFragment : BaseFragment<FragmentMyListBinding>(), ListClickListener {

    private val viewModel by viewModels<MyListViewModel>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyListBinding {
        return FragmentMyListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
        binding!!.btnNewList.elevation = 0f

        binding!!.btnNewList.setOnClickListener {
            val action = MyListFragmentDirections.actionMineToCreation()
            findNavController().navigate(action)
        }
    }

    private fun setLayout() {
        val myListAdapter = MyListAdapter(this)
        binding!!.rvMyList.adapter = myListAdapter

        viewModel.loadLists()
        viewModel.item.observe(viewLifecycleOwner, EventObserver { myLists ->
            myListAdapter.submitList(myLists)
        })
    }

    override fun onClick(list: ListInfo) {
        val action = MyListFragmentDirections.actionMyListToDetail(list)
        findNavController().navigate(action)
    }
}