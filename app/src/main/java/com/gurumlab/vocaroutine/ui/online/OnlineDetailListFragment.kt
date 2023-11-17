package com.gurumlab.vocaroutine.ui.online

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.databinding.FragmentOnlineDetailListBinding
import com.gurumlab.vocaroutine.ui.BaseFragment

class OnlineDetailListFragment : BaseFragment<FragmentOnlineDetailListBinding>() {
    private val args: OnlineDetailListFragmentArgs by navArgs()
    private lateinit var list: ListInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = args.list
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnlineDetailListBinding {
        return FragmentOnlineDetailListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.lifecycleOwner = viewLifecycleOwner
        binding!!.list = list

        setLayout()
        setTopAppBar()
        hideBottomNavigation(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideBottomNavigation(true)
    }

    private fun setLayout() {
        val onlineDetailListAdapter = OnlineDetailListAdapter()
        binding!!.rvDetailList.adapter = onlineDetailListAdapter
        onlineDetailListAdapter.submitList(list.vocabularies)
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