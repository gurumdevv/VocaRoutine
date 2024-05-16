package com.gurumlab.vocaroutine.ui.detail

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
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.gurumlab.vocaroutine.NetworkConnection
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.databinding.FragmentDetailListBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailListFragment : BaseFragment<FragmentDetailListBinding>() {

    private val args: DetailListFragmentArgs by navArgs()
    private lateinit var list: ListInfo
    private val viewModel by viewModels<DetailListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = args.list
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailListBinding {
        return FragmentDetailListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.list = list
        binding.viewModel = viewModel

        setLayout()
        setSnackBar()
        setTopAppBar()
        setNotification()
        setDownloadIcon()
        hideBottomNavigation(false)
    }

    override fun onStop() {
        super.onStop()
        viewModel.resetClickAlarmIcon()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(true)
    }

    private fun setLayout() {
        val detailListAdapter = DetailListAdapter()
        binding.rvDetailList.adapter = detailListAdapter
        detailListAdapter.submitList(list.vocabularies)

        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner, EventObserver { isAvailable ->
            viewModel.setIsNetworkAvailable(isAvailable)
        })

        viewModel.isNetworkAvailable.observe(viewLifecycleOwner, EventObserver { isAvailable ->
            binding.ivSetDownload.isVisible = isAvailable
            binding.ivSetNotification.isVisible = isAvailable
            binding.ivSetUpload.isVisible = isAvailable
        })
    }

    private fun setNotification() {
        var isClickNotificationIcon = false
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isClickAlarmIcon.collect { isClickAlarmIcon ->
                        isClickNotificationIcon = isClickAlarmIcon
                    }
                }

                launch {
                    viewModel.loadAlarm(list)
                    viewModel.isNotificationSet.collect { isNotificationSet ->
                        if (isNotificationSet == true) {
                            binding.ivSetNotification.setImageResource(R.drawable.ic_bell_enabled)
                            if (isClickNotificationIcon) {
                                viewModel.setSnackbarMessage(R.string.set_review_notification_success)
                            }
                        } else if (isNotificationSet == false) {
                            binding.ivSetNotification.setImageResource(R.drawable.ic_bell_disabled)
                            viewModel.setSnackbarMessage(R.string.cancel_review_notification_success)
                        }
                    }
                }
            }
        }
    }

    private fun setDownloadIcon() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadIsDownloaded(list)
            viewModel.isDownloaded
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isDownload ->
                    if (isDownload) {
                        binding.ivSetDownload.setImageResource(R.drawable.ic_download_enabled)
                    } else {
                        binding.ivSetDownload.setImageResource(R.drawable.ic_download_disabled)
                    }
                }
        }
    }

    private fun setSnackBar() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.snackbarMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { messageId ->
                    Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun hideBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }
}