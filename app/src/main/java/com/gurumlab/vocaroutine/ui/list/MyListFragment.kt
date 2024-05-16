package com.gurumlab.vocaroutine.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.NetworkConnection
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.databinding.FragmentMyListBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver
import com.gurumlab.vocaroutine.ui.common.ItemTouchHelperCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        setSnackbarMessage()
    }

    private fun setLayout() {
        val myListAdapter = MyListAdapter(viewModel, this)
        val itemTouchHelper =
            ItemTouchHelper(ItemTouchHelperCallback(myListAdapter, requireContext()))
        itemTouchHelper.attachToRecyclerView(binding.rvMyList)
        binding.rvMyList.adapter = myListAdapter

        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, EventObserver { isAvailable ->
            viewModel.setIsNetworkAvailable(isAvailable)
        })

        viewModel.isNetworkAvailable.observe(viewLifecycleOwner, EventObserver { isAvailable ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    if (isAvailable) {
                        binding.btnNewList.isVisible = true
                        launch {
                            viewModel.onlineItems.collect { onlineItems ->
                                myListAdapter.submitList(onlineItems)
                            }
                        }
                    } else {
                        launch {
                            viewModel.offlineItems.collect { offlineItems ->
                                offlineItems?.let {
                                    myListAdapter.submitList(offlineItems)
                                    binding.tvEmptyDownloaded.isVisible = offlineItems.isEmpty()
                                    binding.ivEmptyMine.isVisible = offlineItems.isEmpty()
                                }
                            }
                        }
                    }

                    launch {
                        viewModel.isError.collect { isError ->
                            binding.ivEmptyMine.isVisible = isError
                            binding.tvEmptyMine.isVisible = isError
                        }
                    }

                    launch {
                        viewModel.isLoading.collect { isLoading ->
                            binding.lottie.isVisible = isLoading
                        }
                    }
                }
            }
        })
    }

    private fun setNewListButton() {
        binding.btnNewList.elevation = 0f

        binding.btnNewList.setOnClickListener {
            val action = MyListFragmentDirections.actionMineToCreation()
            findNavController().navigate(action)
        }
    }

    private fun setSnackbarMessage() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.snackbarMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { messageId ->
                    Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.btn_new_list)
                        .show()
                }
        }
    }

    override fun onClick(list: ListInfo) {
        val action = MyListFragmentDirections.actionMyListToDetail(list)
        findNavController().navigate(action)
    }
}