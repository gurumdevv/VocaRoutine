package com.gurumlab.vocaroutine.ui.online

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
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentOnlineListBinding
import com.gurumlab.vocaroutine.ui.common.ListClickListener
import com.gurumlab.vocaroutine.ui.common.PlusClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnlineListFragment : BaseFragment<FragmentOnlineListBinding>(), ListClickListener,
    PlusClickListener {

    private val viewModel by viewModels<OnlineListViewModel>()

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
    }

    private fun setLayout() {
        val onlineListAdapter = OnlineListAdapter(this, this)
        binding.rvOnlineList.adapter = onlineListAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.sharedList.collect { sharedList ->
                        onlineListAdapter.submitList(sharedList)
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.lottie.isVisible = isLoading
                    }
                }

                launch {
                    viewModel.isError.collect { isError ->
                        binding.ivEmptyOnline.isVisible = isError
                        binding.tvEmptyOnline.isVisible = isError
                    }
                }

                launch {
                    viewModel.isException.collect { isException ->
                        binding.ivConnectionOut.isVisible = isException
                        binding.tvConnectionOut.isVisible = isException
                    }
                }
            }
        }
    }

    private fun setSnackbar() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.snackbarMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { messageId ->
                    Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.bottom_navigation)
                        .show()
                }
        }
    }


    override fun onClick(list: ListInfo) {
        val action = OnlineListFragmentDirections.actionOnlineToOnlineDetail(list)
        findNavController().navigate(action)
    }

    override fun onClickToDownload(list: ListInfo) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getMyLists().collect { myLists ->
                        val isAlreadyExist = viewModel.isAlreadyCreated(myLists, list)
                        if (!isAlreadyExist) viewModel.uploadToMyList(list)
                    }
                }

                launch {
                    viewModel.isEmptyList.collect { isEmptyList ->
                        if (isEmptyList) viewModel.uploadToMyList(list)
                    }
                }
            }
        }
    }
}