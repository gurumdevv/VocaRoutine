package com.gurumlab.vocaroutine.ui.online

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import com.gurumlab.vocaroutine.ui.BaseFragment
import com.gurumlab.vocaroutine.databinding.FragmentOnlineListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OnlineListFragment : BaseFragment<FragmentOnlineListBinding>() {

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

        val onlineListAdapter = OnlineListAdapter()
        binding!!.rvOnlineList.adapter = onlineListAdapter

        lifecycleScope.launch {
            val sharedList = apiClient.getSharedList().values
            sharedList.forEach {
                Log.d("OnlineList", "$it")
                onlineListAdapter.submitList(sharedList.toList())
            }
        }
    }
}