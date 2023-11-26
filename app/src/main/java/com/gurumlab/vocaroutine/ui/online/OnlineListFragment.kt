package com.gurumlab.vocaroutine.ui.online

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.source.local.DataStoreModule
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentOnlineListBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import com.gurumlab.vocaroutine.ui.common.PlusClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnlineListFragment : BaseFragment<FragmentOnlineListBinding>(), ListClickListener,
    PlusClickListener {

    private val viewModel by viewModels<OnlineListViewModel>()

    @Inject
    lateinit var apiClient: ApiClient

    @Inject
    lateinit var dataStore: DataStoreModule

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnlineListBinding {
        return FragmentOnlineListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
        setSnackbar()

        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver { isLoading ->
            binding!!.lottie.isVisible = isLoading
        })

        viewModel.isError.observe(viewLifecycleOwner, EventObserver { isError ->
            binding!!.ivConnectionOut.isVisible = isError
            binding!!.tvConnectionOut.isVisible = isError
        })
    }

    private fun setLayout() {
        val onlineListAdapter = OnlineListAdapter(this, this)
        binding!!.rvOnlineList.adapter = onlineListAdapter

        viewModel.loadLists()
        viewModel.sharedList.observe(viewLifecycleOwner, EventObserver { sharedList ->
            onlineListAdapter.submitList(sharedList.toList())
        })
    }

    private fun setSnackbar() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { messageId ->
            Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT)
                .setAnchorView(R.id.bottom_navigation)
                .show()
        })
    }


    override fun onClick(list: ListInfo) {
        val action = OnlineListFragmentDirections.actionOnlineToOnlineDetail(list)
        findNavController().navigate(action)
    }

    override fun onClickToDownload(list: ListInfo) {
        var isAlreadyExist: Boolean
        viewModel.getMyLists()
        viewModel.myLists.observe(viewLifecycleOwner, EventObserver { myLists ->
            isAlreadyExist = viewModel.isAlreadyCreated(myLists, list)
            if (isAlreadyExist) return@EventObserver
            viewModel.uploadList(list)
        })
    }
}