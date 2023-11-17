package com.gurumlab.vocaroutine.ui.online

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentOnlineListBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnlineListFragment : BaseFragment<FragmentOnlineListBinding>(), ListClickListener {

    private val viewModel by viewModels<OnlineListViewModel>()

    @Inject
    lateinit var apiClient: ApiClient

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnlineListBinding {
        return FragmentOnlineListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
    }

    private fun setLayout() {
        val onlineListAdapter = OnlineListAdapter(this)
        binding!!.rvOnlineList.adapter = onlineListAdapter

        viewModel.loadLists()
        viewModel.item.observe(viewLifecycleOwner, EventObserver { sharedList ->
            onlineListAdapter.submitList(sharedList.toList())
        })
    }

    override fun onClick(list: ListInfo) {
        val action = OnlineListFragmentDirections.actionOnlineToOnlineDetail(list)
        findNavController().navigate(action)
    }
}