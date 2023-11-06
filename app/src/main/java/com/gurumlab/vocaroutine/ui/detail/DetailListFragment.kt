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
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.data.model.MyList
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.VocaRoutineApplication
import com.gurumlab.vocaroutine.data.source.remote.DetailListRepository
import com.gurumlab.vocaroutine.databinding.FragmentDetailListBinding
import com.gurumlab.vocaroutine.ui.common.EventObserver

class DetailListFragment : BaseFragment<FragmentDetailListBinding>() {


    private val args: DetailListFragmentArgs by navArgs()
    private lateinit var list: MyList
    private val viewModel by viewModels<DetailListViewModel> {
        DetailListViewModel.provideFactory(
            application = VocaRoutineApplication.getInstance(),
            repository = DetailListRepository(VocaRoutineApplication.db)
        )
    }

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
        binding!!.lifecycleOwner = viewLifecycleOwner
        binding!!.list = list
        binding!!.viewModel = viewModel

        setLayout()
        setTopAppBar()
        setNotification()
        hideBottomNavigation(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(true)
    }

    private fun setNotification() {
        viewModel.isNotificationSet.observe(viewLifecycleOwner, EventObserver { isNotificationSet ->
            if (isNotificationSet) {
                binding!!.ivSetNotification.setImageResource(R.drawable.ic_bell_enabled)
                Snackbar.make(
                    requireView(),
                    getString(R.string.set_review_notification_success),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                binding!!.ivSetNotification.setImageResource(R.drawable.ic_bell_disabled)
                Snackbar.make(
                    requireView(),
                    getString(R.string.cancel_review_notification_success), Snackbar.LENGTH_SHORT
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
        binding!!.rvDetailList.adapter = detailListAdapter
        detailListAdapter.submitList(list.vocabularies)
    }

    private fun setTopAppBar() {
        binding!!.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun hideBottomNavigation(visible: Boolean) {
        val bottomNavigation = activity?.findViewById<View>(R.id.bottom_navigation)
        bottomNavigation?.isVisible = visible
    }
}