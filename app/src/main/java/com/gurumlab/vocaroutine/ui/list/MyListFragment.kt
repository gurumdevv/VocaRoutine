package com.gurumlab.vocaroutine.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.databinding.FragmentMyListBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver
import com.gurumlab.vocaroutine.ui.common.ItemTouchHelperCallback
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
        setNewListButton()
        setLoadingView()
        setSnackbarMessage()
    }

    private fun setLayout() {
        val myListAdapter = MyListAdapter(viewModel, this)
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(myListAdapter, requireContext()))
        itemTouchHelper.attachToRecyclerView(binding!!.rvMyList)
        binding!!.rvMyList.adapter = myListAdapter

        viewModel.loadLists()
        viewModel.item.observe(viewLifecycleOwner, EventObserver { myLists ->
            myListAdapter.submitList(myLists)
        })
    }

    private fun setNewListButton() {
        binding!!.btnNewList.elevation = 0f

        binding!!.btnNewList.setOnClickListener {
            val action = MyListFragmentDirections.actionMineToCreation()
            findNavController().navigate(action)
        }
    }

    private fun setLoadingView() {
        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver { isLoading ->
            binding!!.lottie.isVisible = isLoading
        })
    }

    private fun setSnackbarMessage() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { messageId ->
            Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT)
                .setAnchorView(R.id.btn_new_list)
                .show()
        })
    }

    override fun onClick(list: ListInfo) {
        val action = MyListFragmentDirections.actionMyListToDetail(list)
        findNavController().navigate(action)
    }
}