package com.gurumlab.vocaroutine.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
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

    private fun setNotification() {
        viewModel.loadAlarm(list)
        viewModel.isNotificationSet.observe(viewLifecycleOwner, EventObserver { isNotificationSet ->
            val isClickNotificationIcon = viewModel.isClickAlarmIcon.value?.content!!
            if (isNotificationSet) {
                binding.ivSetNotification.setImageResource(R.drawable.ic_bell_enabled)
                if (isClickNotificationIcon) {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.set_review_notification_success),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                binding.ivSetNotification.setImageResource(R.drawable.ic_bell_disabled)
                Snackbar.make(
                    requireView(),
                    getString(R.string.cancel_review_notification_success),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })

        viewModel.isNotificationSetError.observe(viewLifecycleOwner, EventObserver { isError ->
            if (isError) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.set_review_notification_fail), Snackbar.LENGTH_SHORT
                ).show()
            }
        })
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
            if (!isAvailable) {
                binding.ivSetDownload.isVisible = false
                binding.ivSetNotification.isVisible = false
                binding.ivSetUpload.isVisible = false
            }
        })
    }

    private fun setSnackBar() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { messageId ->
            Snackbar.make(requireView(), getString(messageId), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun setDownloadIcon() {
        viewModel.loadIsDownloaded(list)
        viewModel.isDownloaded.observe(viewLifecycleOwner, EventObserver { isDownload ->
            if (isDownload) {
                binding.ivSetDownload.setImageResource(R.drawable.ic_download_enabled)
            } else {
                binding.ivSetDownload.setImageResource(R.drawable.ic_download_disabled)
            }
        })
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